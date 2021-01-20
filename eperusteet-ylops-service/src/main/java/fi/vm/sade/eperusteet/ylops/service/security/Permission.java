package fi.vm.sade.eperusteet.ylops.service.security;

public enum Permission {
        LUKU("luku"),
        MUOKKAUS("muokkaus"),
        KOMMENTOINTI("kommentointi"),
        LUONTI("luonti"),
        POISTO("poisto"),
        TILANVAIHTO("tilanvaihto"),
        HALLINTA("hallinta");

        private final String permission;

        private Permission(String permission) {
                this.permission = permission;
        }

        @Override
        public String toString() {
                return permission;
        }
}
