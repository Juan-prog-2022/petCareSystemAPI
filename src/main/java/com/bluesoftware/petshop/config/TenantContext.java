package com.bluesoftware.petshop.config;

import com.bluesoftware.petshop.entities.Veterinarian;

public class TenantContext {

    private static final ThreadLocal<Veterinarian> currentVet = new ThreadLocal<>();

    public static void setCurrentVet(Veterinarian vet) {
        currentVet.set(vet);
    }

    public static Veterinarian getCurrentVet() {
        return currentVet.get();
    }

    public static void clear() {
        currentVet.remove();
    }
}
