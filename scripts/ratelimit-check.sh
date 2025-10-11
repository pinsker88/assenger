for i in $(seq 1 50); do curl -s -w "%{http_code}\n" https://assenger.co/; done | sort | uniq -c
