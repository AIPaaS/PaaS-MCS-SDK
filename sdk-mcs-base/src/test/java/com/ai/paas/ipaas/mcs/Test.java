package com.ai.paas.ipaas.mcs;

import java.util.Optional;

public class Test {

    public static void main(String[] args) throws Exception {
        Object e = "wwwwww";
        Object t = Optional.ofNullable(e).map(Object::toString).orElse(null);
        System.out.println(t);

    }

}
