package com.example.demo.app;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.app.models.UsuarioComentario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.app.models.Comentarios;
import com.example.demo.app.models.Usuario;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner{
private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class); 
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}


	public void ejemploIterable() throws Exception {
		List<String> users = new ArrayList<>();
		users.add("leidy vela");
		users.add("Olix alvarez");
		users.add("julian alvarez");
		users.add("alva villamil");
		Flux<String> nombres = Flux.fromIterable(users);
				//Flux.just("Leidy Vela","Julian Alvarez","Olix Alvarez", "Alba Villamil");
		Flux<Usuario> usuarios = nombres.map(nombre-> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
				.filter(usuario -> usuario.getNombre().equals("Olix"))
				.doOnNext( usuario -> {
					if(usuario == null) {
						throw new RuntimeException("Nombre no puede estar vacio");
					}
					System.out.println(usuario.getNombre());
				}).map(usuario -> {
					String nombre = usuario.getNombre().toUpperCase();
					usuario.setNombre(nombre);
					return usuario;
				});
		usuarios.subscribe(e ->log.info(e.toString()), 
				error->log.error(error.getMessage()), new Runnable() {
					@Override
					public void run() {
						log.info("Ha Finalizado la ejecucion del observable");						
					}
					
				});
	}
	
	public void ejemploToString() throws Exception {
		List<Usuario> users = new ArrayList<>();
		users.add(new Usuario("Leidy","Vela"));
		users.add(new Usuario("Olix","Alvarez"));
		users.add(new Usuario("Julian","Alvarez"));
		users.add(new Usuario("Alba","Villamil"));
		Flux.fromIterable(users)
				.map(usuario-> usuario.getNombre().toLowerCase().concat(" ").concat(usuario.getApellido().toLowerCase()))
				.flatMap(nombre -> {
					if(nombre.contains("v")){
						return Mono.just(nombre);
					}else {
						return Mono.empty();
					}
					})
				.map(usuario -> {
					return usuario;
				}).subscribe(u-> log.info(u.toString()));
	}
	
	public void ejemploFlatMap() throws Exception {
		List<String> users = new ArrayList<>();
		users.add("leidy vela");
		users.add("Olix alvarez");
		users.add("julian alvarez");
		users.add("alva villamil");
		Flux.fromIterable(users)
				.map(nombre-> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
				.flatMap(usuario -> {
					if(usuario.getNombre().equals("Olix")){
						return Mono.just(usuario);
					}else {
						return Mono.empty();
					}
					})
				.map(usuario -> {
					String nombre = usuario.getNombre().toUpperCase();
					usuario.setNombre(nombre);
					return usuario;
				}).subscribe(u-> log.info(u.toString()));
	}
	
	public void ejemploCollectList() throws Exception {
		List<Usuario> users = new ArrayList<>();
		users.add(new Usuario("Leidy","Vela"));
		users.add(new Usuario("Olix","Alvarez"));
		users.add(new Usuario("Julian","Alvarez"));
		users.add(new Usuario("Alba","Villamil"));
		Flux.fromIterable(users)
		.collectList()
		.subscribe(lista->{
			log.info(lista.toString());
			lista.forEach(item-> {
				log.info(item.toString());
			});
			});
	}

	public void ejemploUsuarioFlatMap() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("Olix", "Alvarez"));
		Mono<Comentarios> comentarios = Mono.fromCallable(()->{
			Comentarios coment = new Comentarios();
			coment.setComentarios("Olix es un perro muy juicioso");
			coment.setComentarios("Olix No se orine en esa almuada");
			coment.setComentarios("Olix deje la bulla");
			return  coment;
		});
		usuarioMono.flatMap(usuario -> comentarios.map(comentarios1 -> new UsuarioComentario(usuario, comentarios1)))
				.subscribe(usuarioComentario -> log.info(usuarioComentario.toString()));
	}


	public void ejemploUsuarioZipWith() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("Julian", "Alvarez"));
		Mono<Comentarios> comentarios = Mono.fromCallable(()->{
			Comentarios coment = new Comentarios();
			coment.setComentarios("Olix es un perro muy juicioso");
			coment.setComentarios("Olix No se orine en esa almuada");
			coment.setComentarios("Olix deje la bulla");
			return  coment;
		});
		Mono<UsuarioComentario> usuarioComentarios = usuarioMono.zipWith(comentarios, (usuario, comentarioUsuario)-> new UsuarioComentario(usuario, comentarioUsuario));
		usuarioComentarios.subscribe(usuarioComentario -> log.info(usuarioComentario.toString()));
	}

	public void ejemploUsuarioZipWithForm2() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("Julian", "Alvarez"));
		Mono<Comentarios> comentarios = Mono.fromCallable(()->{
			Comentarios coment = new Comentarios();
			coment.setComentarios("Olix es un perro muy juicioso");
			coment.setComentarios("Olix No se orine en esa almuada");
			coment.setComentarios("Olix deje la bulla");
			return  coment;
		});
		Mono<UsuarioComentario> usuarioComentarios = usuarioMono.zipWith(comentarios)
						.map(tuple -> {
							Usuario u = tuple.getT1();
							Comentarios c = tuple.getT2();
							return new UsuarioComentario(u, c);
						});
		usuarioComentarios.subscribe(usuarioComentario -> log.info(usuarioComentario.toString()));
	}

	
	public void run(String... args) throws Exception {

		ejemploUsuarioZipWithForm2();
	}

}
