package com.micifuz.vets;

import com.micifuz.commons.configuration.ConfigManager;
import com.micifuz.vets.ioc.IoC;

import io.reactivex.Completable;
import io.reactivex.Single;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;

import java.util.Arrays;

public class VetsMainVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(VetsMainVerticle.class.getName());
	private final String HOST = "0.0.0.0";
	private final Integer PORT = 8083;

	@Override
	public Completable rxStart() {
		vertx.exceptionHandler(error -> LOGGER.info(
			error.getMessage() + error.getCause() + Arrays.toString(error.getStackTrace()) + error
				.getLocalizedMessage()));

		Single<Integer> serverPort = ConfigManager.getInstance(vertx).resolveProperty("server.port", PORT);
		Single<Router> serverRouter = IoC.getInstance().getRouting().createRouter();

		return serverRouter.zipWith(serverPort, this::startHttpServer)
				.doOnError(LOGGER::error)
				.flatMapCompletable(started -> started);
	}

	private Completable startHttpServer(Router router, Integer port) {
		return vertx.createHttpServer().requestHandler(router).rxListen(port, HOST).flatMapCompletable(httpServer -> {
			LOGGER.info(String.format("HTTP server started on http://%s:%d", HOST, port));
			return Completable.complete();
		});
	}
}
