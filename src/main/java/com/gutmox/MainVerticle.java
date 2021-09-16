package com.gutmox;

import com.gutmox.ioc.IoC;
import com.gutmox.router.Routing;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import java.util.Arrays;

public class MainVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class.getName());
	private final String HOST = "0.0.0.0";
	private final Integer PORT = 8080;

	@Override
	public Completable rxStart() {
		vertx.exceptionHandler(error -> LOGGER.info(
			error.getMessage() + error.getCause() + Arrays.toString(error.getStackTrace()) + error
				.getLocalizedMessage()));

		return IoC.getInstance().getRouting().createRouter().flatMap(this::startHttpServer)
			.flatMapCompletable(httpServer -> {
				LOGGER.info("HTTP server started on http://{0}:{1}", HOST, PORT.toString());
				return Completable.complete();
			});
	}

	private Single<HttpServer> startHttpServer(Router router) {
		return vertx.createHttpServer().requestHandler(router).rxListen(PORT, HOST);
	}
}
