create table skeduloitu_ajo
(
    id bigint not null primary key,
    nimi varchar(255) not null constraint uk_ibu56vxb4nlaifyuuyoxg6r5k unique,
    status varchar(255) default 'pysaytetty'::character varying not null,
    viimeisin_ajo_kaynnistys timestamp,
    viimeisin_ajo_lopetus timestamp
);