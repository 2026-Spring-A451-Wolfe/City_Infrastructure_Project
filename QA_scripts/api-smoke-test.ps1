[CmdletBinding()]
param(
    [string]$BaseUrl = "http://localhost:8080",
    [switch]$SkipDocker,
    [switch]$NoCache,
    [switch]$FreshDb,
    [switch]$TearDown,
    [string]$LoginIdentity = "citizen_user@nola.gov",
    [string]$LoginPassword = "Citizen!1234",
    [int]$ReportCount = 1,
    [int]$ImagesPerReport = 1,
    [string]$ReportTitlePrefix = "Smoke Test Report",
    [string]$OutputFile = ""
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Write-Step {
    param([string]$Message)
    Write-Host "`n[STEP] $Message" -ForegroundColor Cyan
}

$testResults = New-Object System.Collections.Generic.List[object]

function Add-TestResult {
    param(
        [string]$Name,
        [ValidateSet("PASS", "FAIL", "SKIP")]
        [string]$Status,
        [string]$Details
    )

    $testResults.Add([pscustomobject]@{
        Name    = $Name
        Status  = $Status
        Details = $Details
    })

    if ($Status -eq "PASS") {
        Write-Host "[PASS] $Name - $Details" -ForegroundColor Green
    }
    elseif ($Status -eq "FAIL") {
        Write-Host "[FAIL] $Name - $Details" -ForegroundColor Red
    }
    else {
        Write-Host "[SKIP] $Name - $Details" -ForegroundColor Yellow
    }
}

function Get-OutputPath {
    param([string]$ConfiguredPath)

    if ([string]::IsNullOrWhiteSpace($ConfiguredPath)) {
        $timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
        return Join-Path $PSScriptRoot "qa-test-results-$timestamp.txt"
    }

    if ([System.IO.Path]::IsPathRooted($ConfiguredPath)) {
        return $ConfiguredPath
    }

    return Join-Path $PSScriptRoot $ConfiguredPath
}

function Write-TestReport {
    param(
        [string]$Path,
        [System.Collections.Generic.List[object]]$Results
    )

    $passCount = @($Results | Where-Object { $_.Status -eq "PASS" }).Count
    $failCount = @($Results | Where-Object { $_.Status -eq "FAIL" }).Count
    $skipCount = @($Results | Where-Object { $_.Status -eq "SKIP" }).Count

    $lines = New-Object System.Collections.Generic.List[string]
    $lines.Add("NOLA API QA Test Results")
    $lines.Add("Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")")
    $lines.Add("BaseUrl: $BaseUrl")
    $lines.Add("ReportCount: $ReportCount")
    $lines.Add("ImagesPerReport: $ImagesPerReport")
    $lines.Add("")
    $lines.Add("Summary: PASS=$passCount FAIL=$failCount SKIP=$skipCount TOTAL=$($Results.Count)")
    $lines.Add("")
    $lines.Add("Detailed Results")

    foreach ($result in $Results) {
        $lines.Add("[$($result.Status)] $($result.Name) :: $($result.Details)")
    }

    $parent = Split-Path -Path $Path -Parent
    if (-not [string]::IsNullOrWhiteSpace($parent) -and -not (Test-Path $parent)) {
        New-Item -ItemType Directory -Path $parent | Out-Null
    }

    Set-Content -Path $Path -Value $lines -Encoding UTF8
}

function Invoke-JsonRequest {
    param(
        [ValidateSet("GET", "POST", "PUT", "DELETE")]
        [string]$Method,
        [string]$Uri,
        [hashtable]$Headers,
        [object]$Body
    )

    $requestParams = @{
        Method      = $Method
        Uri         = $Uri
        ContentType = "application/json"
        UseBasicParsing = $true
    }

    if ($Headers) {
        $requestParams.Headers = $Headers
    }

    if ($null -ne $Body) {
        $requestParams.Body = ($Body | ConvertTo-Json -Depth 10)
    }

    try {
        $resp = Invoke-WebRequest @requestParams
        $json = $null
        if ($resp.Content) {
            try { $json = $resp.Content | ConvertFrom-Json } catch { $json = $null }
        }

        return @{
            StatusCode = [int]$resp.StatusCode
            Json       = $json
            Raw        = $resp.Content
            Error      = $false
        }
    }
    catch {
        $response = $_.Exception.Response
        if ($null -ne $response) {
            $statusCode = [int]$response.StatusCode
            $reader = New-Object System.IO.StreamReader($response.GetResponseStream())
            $raw = $reader.ReadToEnd()
            $reader.Dispose()

            $json = $null
            if (-not [string]::IsNullOrWhiteSpace($raw)) {
                try { $json = $raw | ConvertFrom-Json } catch { $json = $null }
            }

            return @{
                StatusCode = $statusCode
                Json       = $json
                Raw        = $raw
                Error      = $true
            }
        }

        return @{
            StatusCode = -1
            Json       = $null
            Raw        = $_.Exception.Message
            Error      = $true
        }
    }
}

function Start-Stack {
    if ($FreshDb) {
        Write-Step "Removing existing containers and volumes"
        docker compose down -v | Out-Host
    }

    if ($NoCache) {
        Write-Step "Building containers with no cache"
        docker compose build --no-cache | Out-Host
        Write-Step "Starting containers"
        docker compose up -d | Out-Host
    }
    else {
        Write-Step "Starting containers with fresh build"
        docker compose up -d --build | Out-Host
    }
}

function Wait-ForHealth {
    param([string]$HealthUrl)

    Write-Step "Waiting for API health endpoint"
    $maxAttempts = 45
    for ($i = 1; $i -le $maxAttempts; $i++) {
        try {
            $resp = Invoke-WebRequest -Method GET -Uri $HealthUrl -TimeoutSec 3 -UseBasicParsing
            if ([int]$resp.StatusCode -eq 200) {
                return $true
            }
        }
        catch {
            Start-Sleep -Seconds 2
        }
    }

    return $false
}

$startedStack = $false
$token = $null
$authHeader = $null
$reportIds = New-Object System.Collections.Generic.List[int]
$outputPath = Get-OutputPath -ConfiguredPath $OutputFile
$totalReportsCreated = 0
$totalImagesUploaded = 0

if ($ReportCount -lt 1) {
    throw "ReportCount must be >= 1"
}

if ($ImagesPerReport -lt 1) {
    throw "ImagesPerReport must be >= 1"
}

try {
    if (-not $SkipDocker) {
        Write-Step "Starting Docker stack"
        try {
            Start-Stack
            $startedStack = $true
            Add-TestResult -Name "Docker Start" -Status "PASS" -Details "Docker stack started successfully"
        }
        catch {
            Add-TestResult -Name "Docker Start" -Status "FAIL" -Details $_.Exception.Message
        }
    }
    else {
        Add-TestResult -Name "Docker Start" -Status "SKIP" -Details "Skipped because -SkipDocker was provided"
    }

    Write-Step "Checking health endpoint"
    $healthUp = Wait-ForHealth -HealthUrl "$BaseUrl/health"
    if ($healthUp) {
        Add-TestResult -Name "GET /health" -Status "PASS" -Details "Health endpoint returned HTTP 200"
    }
    else {
        Add-TestResult -Name "GET /health" -Status "FAIL" -Details "API did not become healthy in time"
    }

    Write-Step "Validating public department endpoint"
    $departments = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/departments" -Headers $null -Body $null
    if ($departments.StatusCode -eq 200 -and $null -ne $departments.Json -and $departments.Json.Count -ge 1) {
        Add-TestResult -Name "GET /api/departments" -Status "PASS" -Details "Department count: $($departments.Json.Count)"
    }
    elseif ($departments.StatusCode -eq 200) {
        Add-TestResult -Name "GET /api/departments" -Status "FAIL" -Details "HTTP 200 but no department data was returned"
    }
    else {
        Add-TestResult -Name "GET /api/departments" -Status "FAIL" -Details "HTTP $($departments.StatusCode). Body: $($departments.Raw)"
    }

    Write-Step "Logging in"
    $loginBody = @{
        emailOrPhone = $LoginIdentity
        password     = $LoginPassword
    }
    $login = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/auth/login" -Headers $null -Body $loginBody
    if ($login.StatusCode -eq 200 -and $null -ne $login.Json -and -not [string]::IsNullOrWhiteSpace($login.Json.token)) {
        $token = $login.Json.token.Trim()
        $authHeader = @{ Authorization = "Bearer $token" }
        Add-TestResult -Name "POST /api/auth/login" -Status "PASS" -Details "Login succeeded and JWT was returned"
    }
    elseif ($login.StatusCode -eq 200) {
        Add-TestResult -Name "POST /api/auth/login" -Status "FAIL" -Details "HTTP 200 but token was missing"
    }
    else {
        Add-TestResult -Name "POST /api/auth/login" -Status "FAIL" -Details "HTTP $($login.StatusCode). Body: $($login.Raw)"
    }

    Write-Step "Validating token via /api/auth/me"
    if ($null -eq $authHeader) {
        Add-TestResult -Name "GET /api/auth/me" -Status "SKIP" -Details "Skipped because login did not return a valid token"
    }
    else {
        $me = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/auth/me" -Headers $authHeader -Body $null
        if ($me.StatusCode -eq 200 -and $null -ne $me.Json -and -not [string]::IsNullOrWhiteSpace($me.Json.role)) {
            Add-TestResult -Name "GET /api/auth/me" -Status "PASS" -Details "Authenticated role: $($me.Json.role)"
        }
        elseif ($me.StatusCode -eq 200) {
            Add-TestResult -Name "GET /api/auth/me" -Status "FAIL" -Details "HTTP 200 but role was missing"
        }
        else {
            Add-TestResult -Name "GET /api/auth/me" -Status "FAIL" -Details "HTTP $($me.StatusCode). Body: $($me.Raw)"
        }
    }

    Write-Step "Validating reports listing endpoint"
    $reports = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/reports" -Headers $null -Body $null
    if ($reports.StatusCode -eq 200) {
        Add-TestResult -Name "GET /api/reports" -Status "PASS" -Details "Reports endpoint returned HTTP 200"
    }
    else {
        Add-TestResult -Name "GET /api/reports" -Status "FAIL" -Details "HTTP $($reports.StatusCode). Body: $($reports.Raw)"
    }

    for ($r = 1; $r -le $ReportCount; $r++) {
        $reportTestName = "POST /api/reports [$r/$ReportCount]"

        if ($null -eq $authHeader) {
            Add-TestResult -Name $reportTestName -Status "SKIP" -Details "Skipped because login did not return a valid token"

            for ($i = 1; $i -le $ImagesPerReport; $i++) {
                Add-TestResult -Name "POST /api/images/{reportId} [$r/$ReportCount image $i/$ImagesPerReport]" -Status "SKIP" -Details "Skipped because report was not created"
            }
            continue
        }

        Write-Step "Creating report $r of $ReportCount"
        $timestamp = Get-Date -Format "yyyyMMdd-HHmmssfff"
        $reportBody = @{
            title       = "$ReportTitlePrefix $timestamp-$r"
            description = "Automated smoke/stress test report #$r"
            category    = "Pothole"
            severity    = "High"
            latitude    = 29.9511
            longitude   = -90.0715
        }

        $createdReport = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/reports" -Headers $authHeader -Body $reportBody
        $reportId = $null

        if ($createdReport.StatusCode -eq 201 -and $null -ne $createdReport.Json -and $createdReport.Json.id) {
            $reportId = [int]$createdReport.Json.id
            $reportIds.Add($reportId)
            $totalReportsCreated++
            Add-TestResult -Name $reportTestName -Status "PASS" -Details "Created report id: $reportId"
        }
        elseif ($createdReport.StatusCode -eq 201) {
            Add-TestResult -Name $reportTestName -Status "FAIL" -Details "HTTP 201 but response id was missing"
        }
        else {
            Add-TestResult -Name $reportTestName -Status "FAIL" -Details "HTTP $($createdReport.StatusCode). Body: $($createdReport.Raw)"
        }

        for ($i = 1; $i -le $ImagesPerReport; $i++) {
            $imageTestName = "POST /api/images/{reportId} [$r/$ReportCount image $i/$ImagesPerReport]"

            if ($null -eq $reportId) {
                Add-TestResult -Name $imageTestName -Status "SKIP" -Details "Skipped because report creation failed"
                continue
            }

            Write-Step "Uploading image $i of $ImagesPerReport for report $reportId"
            $tempPng = Join-Path $env:TEMP "smoke-test-$timestamp-$r-$i.png"
            $pngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9WmB2XwAAAAASUVORK5CYII="
            [System.IO.File]::WriteAllBytes($tempPng, [System.Convert]::FromBase64String($pngBase64))

            $uploadRaw = curl.exe -sS -X POST "$BaseUrl/api/images/$reportId" -H "Authorization: Bearer $token" -F "image=@$tempPng"
            if ([string]::IsNullOrWhiteSpace($uploadRaw)) {
                Add-TestResult -Name $imageTestName -Status "FAIL" -Details "Empty response from image upload"
                Remove-Item -Path $tempPng -Force -ErrorAction SilentlyContinue
                continue
            }

            try {
                $upload = $uploadRaw | ConvertFrom-Json
                if ($upload.success) {
                    $totalImagesUploaded++
                    Add-TestResult -Name $imageTestName -Status "PASS" -Details "Uploaded image id: $($upload.imageId)"
                }
                else {
                    Add-TestResult -Name $imageTestName -Status "FAIL" -Details "Upload did not return success=true. Body: $uploadRaw"
                }
            }
            catch {
                Add-TestResult -Name $imageTestName -Status "FAIL" -Details "Non-JSON upload response. Body: $uploadRaw"
            }

            Remove-Item -Path $tempPng -Force -ErrorAction SilentlyContinue
        }
    }
}
catch {
    Add-TestResult -Name "Script Execution" -Status "FAIL" -Details $_.Exception.Message
}
finally {
    if ($TearDown -and $startedStack) {
        Write-Step "Tearing down containers and volumes"
        try {
            docker compose down -v | Out-Host
            Add-TestResult -Name "Docker TearDown" -Status "PASS" -Details "Containers and volumes removed"
        }
        catch {
            Add-TestResult -Name "Docker TearDown" -Status "FAIL" -Details $_.Exception.Message
        }
    }

    Write-TestReport -Path $outputPath -Results $testResults

    $passCount = @($testResults | Where-Object { $_.Status -eq "PASS" }).Count
    $failCount = @($testResults | Where-Object { $_.Status -eq "FAIL" }).Count
    $skipCount = @($testResults | Where-Object { $_.Status -eq "SKIP" }).Count

    Write-Host "`n=======================================" -ForegroundColor Cyan
    Write-Host "QA test run completed" -ForegroundColor Cyan
    Write-Host "PASS: $passCount  FAIL: $failCount  SKIP: $skipCount" -ForegroundColor Cyan
    Write-Host "Reports created: $totalReportsCreated" -ForegroundColor Cyan
    Write-Host "Images uploaded: $totalImagesUploaded" -ForegroundColor Cyan
    Write-Host "Results file: $outputPath" -ForegroundColor Cyan
    Write-Host "=======================================" -ForegroundColor Cyan

    if ($failCount -gt 0) {
        exit 1
    }

    exit 0
}
