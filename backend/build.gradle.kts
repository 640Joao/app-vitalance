plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.vitalance"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// --- Dependências de Autenticação e Web ---
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// --- Dependências de Persistência (JPA) ---
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// --- DEPENDÊNCIA DO MYSQL (PERSISTENTE) ---
	// Você deve remover a dependência do H2 se ela estiver presente
	runtimeOnly("com.mysql:mysql-connector-j")

	// --- DEPENDÊNCIA DO E-MAIL ---
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// --- Outras dependências padrão do Kotlin/Spring ---
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("org.jetbrains.kotlin:kotlin-stdlib")

	// Configurações de teste, etc.
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
