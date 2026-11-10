#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

SQL_TMP="$ROOT/ylops.sql.tmp"
ZIP="$ROOT/ylops.zip"
ZIP_TMP="$ZIP.tmp"

if ! docker info >/dev/null 2>&1; then
  echo "Docker ei ole käynnissä. Käynnistä Docker Desktop ja yritä uudelleen."
  exit 1
fi

if [ -z "$(docker compose ps -q --status running eperusteet-ylops)" ]; then
  echo "Käynnistetään tietokanta."
  docker compose up -d --wait
fi

rm -f "$SQL_TMP" "$ZIP_TMP"
trap 'rm -f "$SQL_TMP" "$ZIP_TMP"' EXIT

echo "Tallennetaan kanta tiedostoon db/ylops.zip."
# Tavallinen SQL, kiinteä \restrict-avain ja LF. Peräkkäiset ajot tuottavat saman zipin.
# Istuntotaulujen rivit jätetään pois, koska ne vaihtuvat joka kirjautumisella.
docker compose exec -T eperusteet-ylops pg_dump \
  --username=oph \
  --dbname=ylops \
  --format=plain \
  --encoding=UTF8 \
  --no-owner \
  --no-privileges \
  --no-tablespaces \
  --no-security-labels \
  --no-unlogged-table-data \
  --restrict-key=ylops \
  --exclude-table-data=public.spring_session \
  --exclude-table-data=public.spring_session_attributes \
  --exclude-table-data=public.cas_client_session \
  | tr -d '\015' > "$SQL_TMP"

python - "$SQL_TMP" "$ZIP_TMP" <<'PY'
import sys
import zipfile

src, dst = sys.argv[1], sys.argv[2]
info = zipfile.ZipInfo("ylops.sql", date_time=(1980, 1, 1, 0, 0, 0))
info.compress_type = zipfile.ZIP_DEFLATED
info.external_attr = 0o644 << 16
with zipfile.ZipFile(dst, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9) as archive:
    with open(src, "rb") as handle:
        archive.writestr(info, handle.read(), compresslevel=9)
PY

mv "$ZIP_TMP" "$ZIP"
rm -f "$SQL_TMP" "$ROOT/ylops.sql"
trap - EXIT

echo "Valmis: db/ylops.zip"
