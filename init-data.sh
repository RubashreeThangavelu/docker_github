#!/bin/sh

echo "Creating test data..."

# Source folders
mkdir -p /data/tmp
mkdir -p /data/temp
mkdir -p /data/source
mkdir -p /data/test
mkdir -p /data/empty-source
mkdir -p /data/unsupported_files
mkdir -p /data/a
mkdir -p /data/LargeData

# Backup folders
mkdir -p /data/backup
mkdir -p /data/a_backup

# Symlink
ln -sfn /etc /data/etc_link

mkdir /root/source
 chmod 500 /root/source

# Sample files
echo "sample text file" > /data/tmp/file1.txt
echo "another text file" > /data/test/file2.txt
echo "New text file" > /data/source/file3.txt
echo "lock file" > /data/unsupported_files/file4.lock
echo "short path file" > /data/a/file5.txt
echo "sample text file" > /data/temp/file6.txt
echo "New text file" > /data/source/file7.txt

# Temp file
echo "temp content" > /data/source/tempfile.txt

# Large file (100MB)
dd if=/dev/zero of=/data/LargeData/largefile.bin bs=1M count=100

echo "Test data created."
