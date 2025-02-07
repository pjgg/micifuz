package com.micifuz.vets;

import com.micifuz.vets.ioc.IoC;
import io.reactivex.Completable;
import io.reactivex.Single;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;

import java.util.Arrays;

public class MainVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class.getName());
	private final String HOST = "0.0.0.0";
	private final Integer PORT = 8083;

	@Override
	public Completable rxStart() {
		vertx.exceptionHandler(error -> LOGGER.info(
			error.getMessage() + error.getCause() + Arrays.toString(error.getStackTrace()) + error
				.getLocalizedMessage()));

		return IoC.getInstance().getRouting().createRouter().flatMap(this::startHttpServer)
			.flatMapCompletable(httpServer -> {
				LOGGER.info(String.format("HTTP server started on http://%s:%d", HOST, PORT));
				return Completable.complete();
			});
	}

	private Single<HttpServer> startHttpServer(Router router) {
		return vertx.createHttpServer().requestHandler(router).rxListen(PORT, HOST);
	}
}
