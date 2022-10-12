#!/bin/bash

cd opt

wget "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-Country-CSV&license_key=${LISENCE_KEY}&suffix=zip" -O /geolite2legacy/maxmind-geo/GeoLite2-Country-CSV.zip

python /root/geolite2legacy/geolite2legacy.py -i /root/geolite2legacy/maxmind-geo/GeoLite2-Country-CSV.zip -f /root/geolite2legacy/geoname2fips.csv -o /root/geolite2legacy/maxmind-geo/GeoIP.dat

sleep 30s

mv -f /opt/geolite2legacy/maxmind-geo/GeoIP.dat /usr/share/GeoIP

sleep 10s

service nginx reload