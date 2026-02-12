CREATE TABLE poistetut_perusteet (
    peruste_id BIGINT NOT NULL PRIMARY KEY,
    poistettu_aikaleima TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);