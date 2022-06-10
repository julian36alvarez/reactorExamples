package com.example.demo.app.models;

import java.util.ArrayList;
import java.util.List;

public class Comentarios {
	
	private List<String> comentarios;

	public Comentarios() {
		this.comentarios = new ArrayList<>();
	}

	public void setComentarios(String comentario) {
		this.comentarios.add(comentario);
	}

	@Override
	public String toString() {
		return "Comentarios [comentarios=" + comentarios + "]";
	}
	
	
	
	

}
