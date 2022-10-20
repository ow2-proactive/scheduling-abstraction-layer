i=0
while [ `ps aux | grep [l]ock_is_held | wc -l` != 0 ]; do
	echo "Lock_is_held $i"
	ps aux | grep [l]ock_is_held
	sleep 10
	((i=i+10));
done
echo "Exited the while loop, time spent: $i"
echo "ps aux | grep apt"
ps aux | grep apt
echo "Waiting for lock task ended properly."