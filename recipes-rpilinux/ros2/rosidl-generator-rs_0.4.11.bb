DESCRIPTION = "Generate ROS interfaces in Rust"
AUTHOR = "Esteve Fernandez <esteve@apache.org>"
ROS_AUTHOR = "Esteve Fernandez <esteve@apache.org>"
HOMEPAGE = "https://wiki.ros.org"
SECTION = "devel"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://package.xml;beginline=8;endline=8;md5=12c26a18c7f493fdc7e8a93b16b7c04f"

ROS_CN = "rosidl_rust"
ROS_BPN = "rosidl_generator_rs"

inherit ros_distro_jazzy

ROS_BUILD_DEPENDS = ""

ROS_BUILDTOOL_DEPENDS = " \
    ament-cmake-native \
    ros-environment-native \
"

ROS_EXPORT_DEPENDS = " \
    rosidl-generator-c \
"

ROS_BUILDTOOL_EXPORT_DEPENDS = " \
    ament-cmake-native \
    ros-environment-native \
    rosidl-pycommon-native \
    rosidl-typesupport-c-native \
    rosidl-typesupport-interface-native \
"

ROS_EXEC_DEPENDS = " \
    rosidl-generator-c \
    rosidl-parser \
"

ROS_TEST_DEPENDS = " \
    ament-cmake-gtest \
    ament-lint-auto \
    ament-lint-common \
    rosidl-generator-c \
    rosidl-pycommon \
"

DEPENDS = "${ROS_BUILD_DEPENDS} ${ROS_BUILDTOOL_DEPENDS}"
DEPENDS += "${ROS_EXPORT_DEPENDS} ${ROS_BUILDTOOL_EXPORT_DEPENDS}"

RDEPENDS:${PN} += "${ROS_EXEC_DEPENDS}"

ROS_BRANCH ?= "branch=release/jazzy/rosidl_generator_rs"
SRC_URI = "git://github.com/ros2-gbp/rosidl_rust-release;${ROS_BRANCH};protocol=https"
SRCREV = "cd8fadf02d586253a208a7204d2033375f94a68e"
S = "${WORKDIR}/git"

ROS_BUILD_TYPE = "ament_cmake"

inherit ros_${ROS_BUILD_TYPE}

BBCLASSEXTEND = "native"
