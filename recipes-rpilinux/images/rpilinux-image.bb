require recipes-core/images/core-image-base.bb


IMAGE_INSTALL:append = " libstdc++"
TOOLCHAIN_TARGET_TASK:append = " libstdc++-staticdev"
EXTRA_IMAGE_FEATURES = "debug-tweaks"
IMAGE_INSTALL:append = " openssh openssh-sftp-server "
IMAGE_INSTALL:append = " iw wpa-supplicant linux-firmware-bcm43430 packagegroup-base"
#IMAGE_FEATURES:append = " tools-profile tools-debug eclipse-debug"
IMAGE_INSTALL:append = " pkgconfig rust-4motor-drv probe-rs"
IMAGE_INSTALL:append = " ros-core"
IMAGE_INSTALL:append = " rmw-fastrtps-cpp examples-rclrs-minimal-pub-sub"
BOOT_SPACE = "65536"
