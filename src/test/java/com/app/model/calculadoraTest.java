package com.app.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class calculadoraTest {

    calculadora c = null;

    @BeforeEach
    void setUp() {
        c = new calculadora();
        System.out.println("Iniciando la calculadora");
    }

    @Test
    public void given_two_integers_when_addition_then_ok(){
        assertEquals(6,c.calcular(4,2));
        System.out.println(" suma calculadora");
    }

    @Test
    public void given_two_integers_when_division_then_exception(){
        assertThrows(ArithmeticException.class, () -> {
            c.dividir(4, 0);
        });
        System.out.println(" division calculadora - excepción capturada");
    }

}