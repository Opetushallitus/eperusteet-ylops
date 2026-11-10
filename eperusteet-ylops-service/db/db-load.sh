#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

DUMP="$ROOT/ylops.zip"

if ! docker info >/dev/null 2>&1; then
  echo "Docker ei ole käynnissä. Käynnistä Docker Desktop ja yritä uudelleen."
  exit 1
fi

if [ ! -f "$DUMP" ]; then
  echo "Tiedostoa db/ylops.zip ei ole. Aja ensin ./db-save.sh."
  exit 1
fi

if [ -z "$(docker compose ps -q --status running eperusteet-ylops)" ]; then
  echo "Käynnistetään tietokanta."
  docker compose up -d --wait
fi

psql_value() {
  docker compose exec -T eperusteet-ylops psql \
    --username=oph \
    --dbname=postgres \
    --quiet \
    --tuples-only \
    --no-align \
    --command="$1"
}

drop_database_if_exists() {
  local name="$1"
  local exists
  exists="$(psql_value "SELECT 1 FROM pg_database WHERE datname = '${name}'")"
  exists="${exists//[[:space:]]/}"
  if [ "$exists" = "1" ]; then
    docker compose exec -T eperusteet-ylops dropdb --username=oph --force "$name"
  fi
}

echo "Palautetaan dump tietokantaan ylops_load."
drop_database_if_exists ylops_load
docker compose exec -T eperusteet-ylops createdb --username=oph ylops_load
unzip -p "$DUMP" ylops.sql | docker compose exec -T eperusteet-ylops psql \
  --username=oph \
  --dbname=ylops_load \
  --quiet \
  --set=ON_ERROR_STOP=1 \
  >/dev/null

others="$(psql_value "SELECT count(*) FROM pg_stat_activity WHERE datname = 'ylops' AND backend_type = 'client backend'")"
others="${others//[[:space:]]/}"
if [ "$others" != "0" ]; then
  echo "Tietokanta ylops on käytössä (${others} yhteyttä). Pysäytä sovellus ja aja ./db-load.sh uudelleen."
  exit 1
fi

echo "Vaihdetaan palautettu kanta käyttöön."
docker compose exec -T eperusteet-ylops psql \
  --username=oph \
  --dbname=postgres \
  --quiet \
  --set=ON_ERROR_STOP=1 \
  --command="SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'ylops' AND pid <> pg_backend_pid();" \
  --command="ALTER DATABASE ylops RENAME TO ylops_old;" \
  --command="ALTER DATABASE ylops_load RENAME TO ylops;" \
  >/dev/null
drop_database_if_exists ylops_old

echo "Kanta ladattu dumpista db/ylops.zip."
