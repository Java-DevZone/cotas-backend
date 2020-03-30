package com.javadevzone.cotas;

import reactor.core.publisher.Mono;

public class TestWebflux {

    public Mono<String> testandoUmaChamada() {
        Mono<String> httpSource = Mono.just("");

        return httpSource;
    }

    public Mono<Double> soma(Double x, Double y) {
        Mono<Double> soma = Mono.just(x);
        Mono<Double> valorSomado = soma.map(element -> {
            System.out.println("Imprimindo");
            if (true)
                throw new NullPointerException("");

            return element / y;
        });
        return valorSomado.doOnError(Throwable::printStackTrace);
    }

    public static void main(String[] args) {
        TestWebflux webflux = new TestWebflux();
        Mono<Double> soma = webflux.soma(10.0, Double.parseDouble("0"));
        soma.subscribe(System.out::println);

//        Double block = soma.block();
//        System.out.println(block);

        // LAZY
//        Usuario u = findByIdUsuario(1L);

        //u.getPermissoes();
    }

}
