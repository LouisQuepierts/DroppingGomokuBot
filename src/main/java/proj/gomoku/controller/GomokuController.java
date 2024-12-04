package proj.gomoku.controller;

import net.quepierts.papyri.annotation.Controller;
import net.quepierts.papyri.annotation.Handler;
import net.quepierts.papyri.dto.Request;
import net.quepierts.papyri.dto.Response;

import javax.annotation.Nonnull;

@Controller("gomoku")
public class GomokuController {
    @Handler("add")
    public static @Nonnull Response<Boolean> add(int column) {
        return Response.failed("unimplemented");
    }

    @Handler("get")
    public static @Nonnull Response<int[][]> get() {
        return Response.failed("unimplemented");
    }

    @Handler("get/column")
    public static @Nonnull Response<int[]> getColumn(@Nonnull Request<Integer> request) {
        return Response.failed("unimplemented");
    }
}
