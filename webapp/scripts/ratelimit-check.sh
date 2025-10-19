#!/bin/bash
URL="https://assenger.co/"
TOTAL_REQUESTS=200
CONCURRENCY=20

echo "Sending $TOTAL_REQUESTS requests to $URL with $CONCURRENCY concurrent connections..."
output_file=$(mktemp)

seq "$TOTAL_REQUESTS" | xargs -n1 -P"$CONCURRENCY" \
  curl -s -o /dev/null -w "%{http_code}\n" "$URL" > "$output_file"

echo "Status code counts:"
sort "$output_file" | uniq -c

if grep -Eq '^( *[0-9]+ 429| *[0-9]+ 503)$' <(sort "$output_file" | uniq -c); then
  echo "✅ Rate/conn limit triggered (saw 429 and/or 503)."
else
  echo "❌ No limiter status observed."
fi

rm "$output_file"
