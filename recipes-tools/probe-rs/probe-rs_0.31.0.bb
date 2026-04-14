SUMMARY = "probe-rs embedded debug tools"
HOMEPAGE = "https://github.com/probe-rs/probe-rs"
LICENSE = "MIT & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=2c5bc559b2aa92d7814058ced9b3cd8a \
                 file://LICENSE-APACHE;md5=86d3f3a95c324c9479bd8986968f4327"
SRC_URI = "git://github.com/probe-rs/probe-rs.git;branch=master;protocol=https \
           file://.probe-rs.toml \
           file://probe-rs-dap-server.service \
           file://probe-rs.service"
SRCREV = "c00221dae7944919c2fe56ff573ac5abd60a81a9"
S = "${WORKDIR}/git"
do_compile[network] = "1"
inherit cargo_bin systemd

# Optional: limit to CLI tools
CARGO_FEATURES = " remote"
# SystemD configuration
SYSTEMD_SERVICE:${PN} = "probe-rs.service probe-rs-dap-server.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
FILES:${PN} += "/root/.probe-rs.toml"
FILES:${PN} += "${systemd_system_unitdir}/probe-rs.service"
FILES:${PN} += "${systemd_system_unitdir}/probe-rs-dap-server.service"
do_install:append() {
    install -d ${D}/root
    # Copy file from WORKDIR to destination with specific permissions
    install -m 0644 ${WORKDIR}/.probe-rs.toml ${D}/root/.probe-rs.toml
    # Install systemd service file
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/probe-rs.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/probe-rs-dap-server.service ${D}${systemd_system_unitdir}/
}
# Suppress buildpaths QA warnings for Rust binaries
INSANE_SKIP:${PN} += "buildpaths"
INSANE_SKIP:${PN}-dbg += "buildpaths"
INSANE_SKIP:${PN}-dev += "buildpaths"