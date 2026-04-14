FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://wpa_supplicant-wlan0.conf file://51-wireless.network"
SYSTEMD_AUTO_ENABLE = "enable"
FILES:${PN}:append = " ${sysconfdir}/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf \
                        ${systemd_unitdir}/network/51-wireless.network \
                        ${sysconfdir}/systemd/system/multi-user.target.wants/wpa_supplicant-nl80211@wlan0.service \
                        \ "

WIFI_SSID ?= ""
WIFI_PASSWORD ?= ""

do_install:append() {

    # Install network configuration file for systemd
  install -d ${D}${systemd_unitdir}/network/
  install -m 0644 ${WORKDIR}/51-wireless.network ${D}${systemd_unitdir}/network/

  install -d ${D}${sysconfdir}/wpa_supplicant

  sed -i -e 's#@WIFI_SSID@#${WIFI_SSID}#g' ${WORKDIR}/wpa_supplicant-wlan0.conf
  sed -i -e 's#@WIFI_PASSWORD@#${WIFI_PASSWORD}#g' ${WORKDIR}/wpa_supplicant-wlan0.conf

  install -m 0600 ${WORKDIR}/wpa_supplicant-wlan0.conf ${D}${sysconfdir}/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf

  # Make sure the system directory for systemd exists.
  install -d ${D}${sysconfdir}/systemd/system/multi-user.target.wants/

  # Link the service file for autostart.
  ln -s ${systemd_unitdir}/system/wpa_supplicant-nl80211@.service \
  ${D}${sysconfdir}/systemd/system/multi-user.target.wants/wpa_supplicant-nl80211@wlan0.service

}
