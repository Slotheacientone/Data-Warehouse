#!/bin/zsh

password="123456"
username="guest_access"
sshpass -p "$password" scp -o ConnectTimeout=5 -P 2227 $username@drive.ecepvn.org:/volume1/ECEP/song.nguyen/DW_2020/data/$1 /home/slo/DataWarehouse/temp/
