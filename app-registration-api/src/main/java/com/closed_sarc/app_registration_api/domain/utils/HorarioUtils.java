package com.closed_sarc.app_registration_api.domain.utils;

import com.closed_sarc.app_registration_api.domain.entities.Horario;
import java.time.LocalTime;

/**
 * Utilitário para conversão de enum Horario para LocalTime.
 * Garante consistência no mapeamento de horários em todo o sistema.
 * 
 * Mapeamento: Horario.A = 08:00, Horario.B = 09:00, etc.
 */
public class HorarioUtils {

    private static final int HORA_BASE = 8; // Começa às 8h

    /**
     * Converte o enum Horario para LocalTime.
     * 
     * @param horario Enum Horario (A, B, C, etc.)
     * @return LocalTime correspondente (A=08:00, B=09:00, C=10:00, etc.)
     */
    public static LocalTime obterHoraInicioPorHorario(Horario horario) {
        int indice = horario.ordinal();
        return LocalTime.of(HORA_BASE + indice, 0);
    }
}

