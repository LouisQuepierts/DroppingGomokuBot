package proj.gomoku.controller;

import net.quepierts.papyri.annotation.Controller;
import net.quepierts.papyri.annotation.Handler;
import net.quepierts.papyri.annotation.Parameter;
import net.quepierts.papyri.dto.Response;

import java.util.Date;

@Controller("test")
public class TestController {
    @Handler("interface/I")
    public static Response<Void> interfaceI(int value) {
        System.out.println(value);
        return Response.success(null);
    }

    @Handler("interface/II")
    public static Response<Void> interfaceII(String key, String value) {
        System.out.println("key " + key);
        System.out.println("value " + value);
        return Response.success(null);
    }

    @Handler("interface/III")
    public static Response<Void> interfaceIII(
            @Parameter("person") PersonalInformation param1,
            @Parameter(value = "date", nullable = true) Date param2
    ) {
        System.out.println(param1);
        System.out.println(param2);
        return Response.success(null);
    }

    public record PersonalInformation(
            String name,
            String gender,
            int age
    ) {}
}
