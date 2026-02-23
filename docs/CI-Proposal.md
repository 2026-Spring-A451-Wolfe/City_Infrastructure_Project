CI Proposal - DevOpps Project

I think two options make the most sense for continuous integration in our project: Jenkins or GitHub Actions.

If we go with Jenkins we'd need a machine running it at all times. We could either install it directly on a Windows PC like a classroom or lab computer, which is easier to get going but ties us to that one machine. An arguably better way to do jenkins is we could run an Ubuntu Server VM on that machine using VirtualBox or Hyper-V and install Jenkins inside the VM. The VM route takes more setup upfront but keeps everything isolated and gives us a Linux environment which is closer to what most CI servers actually run in production. Either way whichever machine is hosting it needs to keep it on and connected or CI goes down for everyone.

With GitHub Actions we'd add a workflow file to the repo and GitHub handles the rest. Every push or pull request automatically runs the same compile, test, and docker steps on GitHub's servers and shows pass or fail right on the pull request page. Every run is on a fresh VM so there's no "works on my machine" situation. It's one file to set up, nothing to maintain, runs 24/7 without depending on anyone's computer being on, and we can turn on branch protection to block merging if CI fails.

I think GitHub Actions makes more sense for us. We don't have to worry about one person's machine being the single point of failure for the whole team's CI, we skip all the Jenkins server and plugin setup, and everyone gets immediate feedback right on the pull request without going to a separate dashboard. It still fully covers the CI requirement. That said I have the setup ready for both so we can go either way.
