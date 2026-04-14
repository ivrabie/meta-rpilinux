# meta-rpilinux Layer Description

This document describes what the `meta-rpilinux` layer contains and how it behaves in this workspace.

## Layer Role and Scope

`meta-rpilinux` is a custom Yocto/OpenEmbedded layer focused on Raspberry Pi images that combine:

- Core Linux system configuration for RPi targets.
- ROS 2 Jazzy packages and integration points.
- Rust-based applications and ROS-Rust support.
- Device/service tooling (networking, probe-rs, FTP).

The layer is structured around recipe groups under:

- `recipes-rpilinux/` for image and custom app content.
- `recipes-core/` and `recipes-connectivity/` for base system and network behavior.
- `recipes-tools/` for additional tool/service packages.

## Layer Registration and Global Defaults

Defined in `sources/meta-rpilinux/conf/layer.conf`:

- Registers layer collection as `meta-rpilinux`.
- Includes all `.bb` and `.bbappend` files under `recipes-*/*/`.
- Priority: `BBFILE_PRIORITY_meta-rpilinux = "6"`.
- Compatible Yocto series: `mickledore scarthgap`.
- Depends on these layers:
  - `core`
  - `meta-python`
  - `openembedded-layer`
  - `ros-common-layer`
  - `ros2-layer`
  - `ros2-jazzy-layer`
  - `rust-bin-layer`

Also sets layer-level build defaults and machine-related settings:

- `ENABLE_SPI_BUS = "1"`
- `ENABLE_I2C = "1"`
- `ENABLE_UART = "1"`
- `BOOT_DELAY = "0"`
- `BOOT_DELAY_MS = "0"`
- `DISABLE_OVERSCAN = "1"`
- `DISABLE_SPLASH = "1"`
- `IMAGE_FSTYPES = "tar.xz ext3 ext4 rpi-sdimg"`
- `KERNEL_MODULE_AUTOLOAD:rpi += "i2c-dev i2c-bcm2708"`
- `INIT_MANAGER = "systemd"`
- `DISTRO_FEATURES:append = " wifi"`
- ROS defaults:
  - `ROS_VERSION ??= "2"`
  - `ROS_DISTRO ??= "jazzy"`

## Image Provided by the Layer

The image recipe is `sources/meta-rpilinux/recipes-rpilinux/images/rpilinux-image.bb`.

It is based on `core-image-base` and appends package content to include:

- Runtime/Toolchain:
  - `libstdc++`
  - `libstdc++-staticdev` (toolchain target task)
- Debug and access:
  - `debug-tweaks`
  - `openssh`
  - `openssh-sftp-server`
- Wireless/network packages:
  - `iw`
  - `wpa-supplicant`
  - `linux-firmware-bcm43430`
  - `packagegroup-base`
- Custom/project packages:
  - `rust-4motor-drv`
  - `probe-rs`
- ROS 2 packages:
  - `ros-core`
  - `rmw-fastrtps-cpp`
  - `examples-rclrs-minimal-pub-sub`

The image also sets `BOOT_SPACE = "65536"`.

## ROS 2 Jazzy and Rust Integration

The layer adds ROS-Rust and Rust demo integration through recipes in `sources/meta-rpilinux/recipes-rpilinux/ros2/`.

### `rosidl-generator-rs_0.4.11.bb`

- Provides `rosidl_generator_rs` for Jazzy.
- Source from `ros2-gbp/rosidl_rust-release` release branch.
- Build type: `ament_cmake`.
- Extended as both target and native (`BBCLASSEXTEND = "native"`).

### `rosidl-core-generators_0.2.0-3.bbappend`

- Appends `rosidl-generator-rs-native` to `ROS_BUILDTOOL_EXPORT_DEPENDS`.
- Integrates the Rust generator into core ROSIDL generator export dependencies.

### `test-msgs_2.0.3-1.bbappend`

- Appends ROS message dependencies used for `test-msgs`:
  - `action-msgs`
  - `service-msgs`
  - `unique-identifier-msgs`
  - `rosidl-typesupport-c`
  - `rosidl-typesupport-cpp`

### `examples-rclrs-minimal-pub-sub_0.5.0.bb`

- Builds ROS 2 Rust example publisher/subscriber binaries from `ros2-rust/examples`.
- Uses `cargo_bin` plus ROS classes (`ros_distro`, `ros_component`, `ros_opt_prefix`).
- Installs binaries into ROS libexec path and installs package metadata into ROS share path.
- Sets compile-time environment for ROS/cmake prefixes and bindgen includes.
- Uses template file `files/cargo-config-ros-crates.toml.in` to map ROS Rust crates from staged sysroot paths.

The crate mapping template includes local-path patches for crates such as:

- `example_interfaces`
- `builtin_interfaces`
- `action_msgs`
- `service_msgs`
- `unique_identifier_msgs`
- `rosgraph_msgs`
- `test_msgs`
- `rcl_interfaces`
- `type_description_interfaces`
- `rmw_dds_common`

## Custom Rust Application Recipes

Located in `sources/meta-rpilinux/recipes-rpilinux/rust/`:

- `rust-4motor-drv_0.1.0.bb`
- `dlt-test-rs_0.1.0.bb`
- `shal_0.1.0.bb`

Common characteristics:

- Generated from cargo-bitbake format.
- Use `cargo`/`cargo_bin` classes.
- Pull main application sources from Git repositories.
- Vendor crate dependencies through `crate://` entries.

`dlt-test-rs` and `shal` include:

- `DEPENDS += " dlt-daemon"`
- `BINDGEN_EXTRA_CLANG_ARGS` pointing at staged include paths.
- Additional local Cargo path (`EXTRA_OECARGO_PATHS`) for `dlt-wrapper-rs` fetched from Git.

## Systemd and Network Configuration

### `systemd_%.bbappend`

In `sources/meta-rpilinux/recipes-core/systemd/systemd_%.bbappend`:

- Appends `networkd` and `resolved` PACKAGECONFIG options.
- Appends runtime dependency on `wpa-supplicant`.

### `systemd-conf_%.bbappend`

In `sources/meta-rpilinux/recipes-core/systemd-conf/systemd-conf_%.bbappend`:

- Adds layer file search path.
- Extends `SRC_URI` with three network unit files:
  - `eth.network`
  - `en.network`
  - `wlan.network`
- Installs those files into `${sysconfdir}/systemd/network`.
- Extends `FILES:${PN}` to package those installed network units.

The unit files define DHCPv4 setup and interface matching:

- `eth.network`: matches `eth*`
- `en.network`: matches `en*`
- `wlan.network`: matches `wlan*`

### `wpa-supplicant_%.bbappend`

In `sources/meta-rpilinux/recipes-connectivity/wpa-supplicant/wpa-supplicant_%.bbappend`:

- Appends source files:
  - `wpa_supplicant-wlan0.conf`
  - `51-wireless.network`
- Enables systemd auto-enable behavior.
- Installs:
  - wireless network file under `${systemd_unitdir}/network/`
  - WPA supplicant config for wlan0 under `${sysconfdir}/wpa_supplicant/`
  - symlink in `multi-user.target.wants` for `wpa_supplicant-nl80211@wlan0.service`

`51-wireless.network` matches `wlan0` and sets IPv4 DHCP.

## probe-rs Tooling and Services

Recipe: `sources/meta-rpilinux/recipes-tools/probe-rs/probe-rs_0.31.0.bb`.

What it provides:

- Builds `probe-rs` tools from Git (`probe-rs/probe-rs`).
- Uses Cargo and systemd integration (`inherit cargo_bin systemd`).
- Enables two services:
  - `probe-rs.service`
  - `probe-rs-dap-server.service`
- Installs `/root/.probe-rs.toml` and both unit files.
- Service commands:
  - `probe-rs serve`
  - `probe-rs dap-server --ip 0.0.0.0 --port 50000`

The bundled `.probe-rs.toml` configures server address and port.

## FTP Service Recipe

Recipe: `sources/meta-rpilinux/recipes-rpilinux/recipes-extended/vsftpd/vsftpd_%.bb`.

Behavior defined by the recipe:

- Builds `vsftpd` from upstream FTP tarball.
- Installs custom config file `rpi_vsftpd.conf` into `/etc/vsftpd.conf`.
- Creates `/home/ftpuser` in the image root during install task.
- Adds user entry data into `${D}/etc/passwd` during install task.

The associated config file `rpi_vsftpd.conf` enables local user login, write access, chroot behavior, logging, and uses `/home/ftpuser` as `local_root`.

## Layer Content Map

High-level map of key files:

- `sources/meta-rpilinux/conf/layer.conf`
- `sources/meta-rpilinux/recipes-rpilinux/images/rpilinux-image.bb`
- `sources/meta-rpilinux/recipes-rpilinux/ros2/`
  - `rosidl-generator-rs_0.4.11.bb`
  - `rosidl-core-generators_0.2.0-3.bbappend`
  - `test-msgs_2.0.3-1.bbappend`
  - `examples-rclrs-minimal-pub-sub_0.5.0.bb`
  - `files/cargo-config-ros-crates.toml.in`
- `sources/meta-rpilinux/recipes-rpilinux/rust/`
  - `rust-4motor-drv_0.1.0.bb`
  - `dlt-test-rs_0.1.0.bb`
  - `shal_0.1.0.bb`
- `sources/meta-rpilinux/recipes-core/systemd/systemd_%.bbappend`
- `sources/meta-rpilinux/recipes-core/systemd-conf/systemd-conf_%.bbappend`
- `sources/meta-rpilinux/recipes-connectivity/wpa-supplicant/wpa-supplicant_%.bbappend`
- `sources/meta-rpilinux/recipes-tools/probe-rs/probe-rs_0.31.0.bb`
- `sources/meta-rpilinux/recipes-rpilinux/recipes-extended/vsftpd/vsftpd_%.bb`

## Summary

`meta-rpilinux` is a Raspberry Pi-oriented integration layer that combines system configuration, network behavior, ROS 2 Jazzy integration, Rust/ROS-Rust build flows, custom Rust applications, and auxiliary runtime services into a single Yocto layer.
