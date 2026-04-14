# FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# SRC_URI += " \
#     file://eth.network \
#     file://en.network \
#     file://wlan.network \
# "



# do_install:append() {
#     install -d ${D}${sysconfdir}/systemd/network
#     install -m 0644 ${WORKDIR}/eth.network ${D}${sysconfdir}/systemd/network
#     install -m 0644 ${WORKDIR}/en.network ${D}${sysconfdir}/systemd/network
#     install -m 0644 ${WORKDIR}/wlan.network ${D}${sysconfdir}/systemd/network
# }

# FILES_${PN} += " \
#     ${sysconfdir}/systemd/network/eth.network \
#     ${sysconfdir}/systemd/network/en.network \
#     ${sysconfdir}/systemd/network/wlan.network \
# "

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://eth.network \
    file://en.network \
    file://wlan.network \
"

do_install:append() {
    install -d ${D}${sysconfdir}/systemd/network
    install -m 0644 ${WORKDIR}/eth.network ${D}${sysconfdir}/systemd/network
    install -m 0644 ${WORKDIR}/en.network ${D}${sysconfdir}/systemd/network
    install -m 0644 ${WORKDIR}/wlan.network ${D}${sysconfdir}/systemd/network
}

FILES:${PN} += " \
    ${sysconfdir}/systemd/network/eth.network \
    ${sysconfdir}/systemd/network/en.network \
    ${sysconfdir}/systemd/network/wlan.network \
"

# FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# SRC_URI:append = " file://wireless.network"

# FILES:${PN}:append = " \
#     ${sysconfdir}/systemd/network/wireless.network \
# "

# do_install:append() {
#         install -d ${D}${sysconfdir}/systemd/network
#         install -m 0755 ${WORKDIR}/wireless.network ${D}${sysconfdir}/systemd/network
# }