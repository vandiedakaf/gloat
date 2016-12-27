package com.vdda.controller;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by francois
 * on 2016-12-26
 * for vandiedakaf solutions
 */
public class CommandsHandlerTest {
    @Test
    public void noCommand() throws Exception {

        String parameters = "";

        String response = (new CommandsHandler().run(parameters)).toString();

        System.out.println(response);
        assertTrue(response.contains("The available gloat commands are"));
    }


    @Test
    public void containsText() throws Exception {

        String parameters = "text=test";

        String response = (new CommandsHandler().run(parameters)).toString();

        assertTrue(response.contains("The available gloat commands are"));
    }
}
