SUMMARY = "ROS 2 Rust minimal publisher/subscriber demo"
DESCRIPTION = "Builds the ros2-rust minimal_pub_sub demo as a ROS package on Yocto"
HOMEPAGE = "https://github.com/ros2-rust/examples"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit cargo_bin ros_distro ros_component ros_opt_prefix

SRC_URI = " \
    git://github.com/ros2-rust/examples.git;protocol=https;branch=main \
    file://cargo-config-ros-crates.toml.in \
"
SRCREV = "35e062c8fed455723273ba68071fca3f744e068b"

S = "${WORKDIR}/git"

PV = "0.5.0+git"

CARGO_SRC_DIR = "rclrs/minimal_pub_sub"
CARGO_MANIFEST_PATH = "${S}/${CARGO_SRC_DIR}/Cargo.toml"
CARGO_INSTALL_DIR = "${D}${ros_libexecdir}"
EXTRA_CARGO_FLAGS = "--bin minimal_publisher --bin minimal_subscriber"

DEPENDS += " \
    llvm-native \
    rcl \
    rcl-action \
    rcl-yaml-param-parser \
    rcutils \
    rmw \
    rmw-implementation \
    rosgraph-msgs \
    test-msgs \
    example-interfaces \
"

RDEPENDS:${PN} += " \
    rcl \
    rcl-action \
    rcl-yaml-param-parser \
    rcutils \
    rmw \
    rmw-implementation \
    rmw-fastrtps-cpp \
    rosgraph-msgs \
    test-msgs \
    example-interfaces \
"

do_compile[network] = "1"

do_compile:prepend() {
    # rclrs build scripts expect a ROS environment and ament/cmake prefix paths
    # pointing at the staged target sysroot.
    export ROS_DISTRO="${ROS_DISTRO}"
    export AMENT_PREFIX_PATH="${STAGING_DIR_HOST}${ros_prefix}"
    export CMAKE_PREFIX_PATH="${STAGING_DIR_HOST}${ros_prefix}:${STAGING_DIR_HOST}${prefix}"

    # Bindgen needs libclang and target sysroot includes when generating FFI.
    export LIBCLANG_PATH="${STAGING_LIBDIR_NATIVE}"
    export BINDGEN_EXTRA_CLANG_ARGS="--sysroot=${STAGING_DIR_HOST} -I${STAGING_DIR_HOST}${includedir} -I${STAGING_DIR_HOST}${ros_includedir}"

    # Use a static Cargo source patch file to map ROS Rust interface crates
    # from Yocto sysroot instead of crates.io.
    install -d ${CARGO_HOME}
    sed "s|@ROS_SYSROOT_SHARE@|${STAGING_DIR_HOST}${ros_datadir}|g" \
        ${WORKDIR}/cargo-config-ros-crates.toml.in > ${CARGO_HOME}/config.toml
}

do_install:append() {
    install -d ${D}${ros_datadir}/${ROS_BPN}
    install -m 0644 ${S}/${CARGO_SRC_DIR}/package.xml ${D}${ros_datadir}/${ROS_BPN}/package.xml

    if [ -d ${S}/${CARGO_SRC_DIR}/launch ]; then
        cp -r --no-preserve=ownership ${S}/${CARGO_SRC_DIR}/launch ${D}${ros_datadir}/${ROS_BPN}/
    fi

    install -d ${D}${ros_datadir}/ament_index/resource_index/packages
    : > ${D}${ros_datadir}/ament_index/resource_index/packages/${ROS_BPN}
}

FILES:${PN} += " \
    ${ros_libexecdir}/* \
    ${ros_datadir}/${ROS_BPN} \
    ${ros_datadir}/ament_index/resource_index/packages/${ROS_BPN} \
"
