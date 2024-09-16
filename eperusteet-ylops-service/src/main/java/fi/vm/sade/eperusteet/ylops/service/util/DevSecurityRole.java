package fi.vm.sade.eperusteet.ylops.service.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DevSecurityRole {

    private String role;

    public static DevSecurityRole ylops() {
        DevSecurityRole instance = new DevSecurityRole();
        instance.role = "EPERUSTEET_YLOPS";
        return instance;
    }

    public static DevSecurityRole eperusteet() {
        DevSecurityRole instance = new DevSecurityRole();
        instance.role = "EPERUSTEET";
        return instance;
    }

    public static DevSecurityRole maarays() {
        DevSecurityRole instance = new DevSecurityRole();
        instance.role = "EPERUSTEET_MAARAYS";
        return instance;
    }

    public static DevSecurityRole amosaa() {
        DevSecurityRole instance = new DevSecurityRole();
        instance.role = "EPERUSTEET_AMOSAA";
        return instance;
    }

    public static DevSecurityRole vst() {
        DevSecurityRole instance = new DevSecurityRole();
        instance.role = "EPERUSTEET_VST";
        return instance;
    }

    public static DevSecurityRole koto() {
        DevSecurityRole instance = new DevSecurityRole();
        instance.role = "EPERUSTEET_KOTO";
        return instance;
    }

    public static DevSecurityRole tuva() {
        DevSecurityRole instance = new DevSecurityRole();
        instance.role = "EPERUSTEET_TUVA";
        return instance;
    }

    public DevSecurityRole admin() {
        this.role = this.role + "_ADMIN";
        return this;
    }

    public DevSecurityRole crud() {
        this.role = this.role + "_CRUD";
        return this;
    }

    public DevSecurityRole read() {
        this.role = this.role + "_READ";
        return this;
    }

    public DevSecurityRole oid(String oid) {
        this.role = this.role + "_" + oid;
        return this;
    }

    public String build() {
        return "APP_" + this.role;
    }

}
