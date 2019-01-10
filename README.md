This is repo for the main application. If you are looking for the component used in this app, please visit [git-static](https://github.com/EXALAB/git-static) and [git-lfs-static](https://github.com/EXALAB/git-static)

# AxGit
Most complete Git CLI client for Android, including [Git LFS (Large File System)](https://git-lfs.github.com) Support

<a href='https://play.google.com/store/apps/details?id=exa.free.ag'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height="100"/></a>

Or download it [from Github](https://github.com/EXALAB/AxGit/releases) if you don't have access to Play Store.



## How it works

We compile the git and git-lfs binary statically using chroot environment in qemu, which allow it to run on Android without compatibility issue, for more details about how it compiled, please visit [git-static](https://github.com/EXALAB/git-static) and [git-lfs-static](https://github.com/EXALAB/git-static).


## Features:

Include all Git CLI Feature, such as:

clone
commit
checkout
push
merge
branch
add
apply
annotate
submodule
svn
and too much to specify here...

Git LFS (Large File System) Support, a feature allow to upload file up to 100mb to your repo, this feature does not available for mips devices due to a bug.


Note : 

1. This app required a Terminal Emulator to work, it could be install on Play Store.

2. Supported architecture: All Android device architecture

3. LFS (Large File System) not working on mips device.

4. For any suggestion or issue, please open an issue on Github, or email us.


## Special Thanks

[QEMU](https://www.qemu.org)