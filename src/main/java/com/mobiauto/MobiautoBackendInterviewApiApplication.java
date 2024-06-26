package com.mobiauto;

import com.mobiauto.job.CadastrarEntidadesJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MobiautoBackendInterviewApiApplication {

	public MobiautoBackendInterviewApiApplication(CadastrarEntidadesJob job) {
		this.job = job;
	}

	public static void main(String[] args) {
		SpringApplication.run(MobiautoBackendInterviewApiApplication.class, args);
	}

	private final CadastrarEntidadesJob job;


	public void run(String... args) {

		job.inserirRolesNoBanco();
		job.inserirAdminNoBanco();

	}

}
