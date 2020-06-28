#!/bin/zsh

password="123456"
username="guest_access"
file="drive.ecepvn.org:/volume1/ECEP/song.nguyen/DW_2020/data/sinhvien_{chieu,sang}_nhom*.xlsx"
sshpass -p "$password" scp -P 2227 $username@$file data/
