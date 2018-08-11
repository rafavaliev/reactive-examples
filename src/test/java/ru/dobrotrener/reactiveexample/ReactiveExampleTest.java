package ru.dobrotrener.reactiveexample;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReactiveExampleTest {

    private Person michael = new Person("Michael", "Weston");
    private Person fiona = new Person("Fiona", "Glenanne");
    private Person sam = new Person("Sam", "Axe");
    private Person jesse = new Person("Jesse", "Porter");
    private Person rafael = new Person("Rafael", "Valiev");

    @Test
    public void monoTests() throws Exception {

        //create person mono
        Mono<Person> personMono = Mono.just(michael);

        // get Person object from mono publisher
        Person person = personMono.block();

        // Log name
        log.info(person.sayMyName());
    }

    @Test
    public void monoTransformTest() throws Exception {
        //create new person mono
        Mono<Person> personMono = Mono.just(fiona);

        PersonCommand command = personMono
                .map(person -> {
                    return new PersonCommand(person);
                }).block();
        log.info(command.sayMyName());

    }

    @Test(expected = NullPointerException.class)
    public void monoFilterTest() throws Exception {
        Mono<Person> personMono = Mono.just(sam);

        Person samAxe = personMono
                .filter(person -> person.getFirstName().equalsIgnoreCase("foo"))
                .block();
        log.info(samAxe.sayMyName()); //throws NPE

    }

    @Test
    public void fluxTest() {
        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.subscribe(person -> log.info(person.sayMyName()));
    }


    @Test
    public void fluxFilterTest() {
        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.filter(person -> person.getFirstName().equalsIgnoreCase(fiona.getFirstName()))
                .subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestDelayNoOutput() throws Exception{
        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.delayElements(Duration.ofSeconds(1))
                .subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestDelayNoOutput2() throws Exception{
        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);
        List<Person> people1 = new ArrayList<>();

        people.delayElements(Duration.ofSeconds(1))
                .subscribe(person -> people1.add(person));

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(1, TimeUnit.SECONDS);

        if(people1.size() == 0) {
            log.info(rafael.sayMyName());
        } else {
            people1.forEach(person -> log.info(person.sayMyName()));
        }

    }
    @Test
    public void fluxTestDelay() throws Exception  {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();
    }

    @Test
    public void fluxTestFilterDelay() throws Exception  {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.delayElements(Duration.ofSeconds(1))
                .filter(person -> person.getFirstName().contains("i"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();
    }

}
