drop trigger if exists tg_refresh_opetussuunnitelma_data_view_after_tila_update on opetussuunnitelma;

CREATE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update
    AFTER UPDATE ON opetussuunnitelma
    FOR EACH ROW
    WHEN (NEW.tila <> OLD.tila)
    EXECUTE PROCEDURE tg_refresh_julkaistu_opetussuunnitelma_data_view();