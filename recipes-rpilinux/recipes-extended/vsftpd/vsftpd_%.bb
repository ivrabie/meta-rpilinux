DESCRIPTION = "FTP server"
SECTION = "network"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

SRC_URI = "ftp://ftp.beasts.org/pub/vsftpd/vsftpd-${PV}.tar.gz"

SRC_URI[md5sum] = "8c4aa8bf06d26abaa8c01326874bc799"
SRC_URI[sha256sum] = "0ecf1cf7e3b6d6cfc4b204c91caadf9e2c0156ab28c8ce4f4e7ee607b87a7290"

inherit autotools systemd useradd update-rc.d

PACKAGES += "${PN}-config"
FILES_${PN}-config = "/etc/vsftpd.conf"

do_install:append() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/rpi_vsftpd.conf ${D}${sysconfdir}/vsftpd.conf

    # Create the FTP user (e.g., 'ftpuser') with a home directory
    install -d ${D}/home/ftpuser
    echo "ftpuser:x:1000:1000:FTP User:/home/ftpuser:/bin/bash" > ${D}/etc/passwd

    # Set the password for the FTP user
    echo "ftpuser:password" | chpasswd -R ${D}/
}

FILES_${PN} += "/etc/vsftpd.conf"