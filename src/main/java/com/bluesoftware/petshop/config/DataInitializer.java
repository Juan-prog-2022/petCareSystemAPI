package com.bluesoftware.petshop.config;

import com.bluesoftware.petshop.entities.*;
import com.bluesoftware.petshop.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;
    private final ProductRepository productRepository;
    private final AppointmentRepository appointmentRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CustomerRepository customerRepository,
                           PetRepository petRepository,
                           ProductRepository productRepository,
                           AppointmentRepository appointmentRepository,
                           VeterinarianRepository veterinarianRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
        this.productRepository = productRepository;
        this.appointmentRepository = appointmentRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // ── USERS ──────────────────────────────────────────────────

        User admin = userRepository.save(User.builder()
                .name("Juan Quiroz")
                .email("admin@petshop.com")
                .password(passwordEncoder.encode("Admin123!"))
                .role(Role.ADMIN)
                .active(true)
                .build());

        User vet = userRepository.save(User.builder()
                .name("Dr. García")
                .email("vet@petshop.com")
                .password(passwordEncoder.encode("Vet123!"))
                .role(Role.VETERINARIAN)
                .active(true)
                .build());

        User customerUser = userRepository.save(User.builder()
                .name("Cliente Demo")
                .email("cliente@petshop.com")
                .password(passwordEncoder.encode("Cliente123!"))
                .role(Role.CUSTOMER)
                .active(true)
                .build());

        // ── VETERINARIAN ────────────────────────────────────────────

        veterinarianRepository.save(Veterinarian.builder()
                .matricula("MP-12345")
                .slug("vetdogarcia")
                .specialty("Cirugía general")
                .address("Av. Siempre Viva 742")
                .city("Springfield")
                .latitude(-34.6037)
                .longitude(-58.3816)
                .user(vet)
                .approved(true)
                .build());

        // ── CUSTOMER ───────────────────────────────────────────────

        Customer customer = customerRepository.save(Customer.builder()
                .name("Cliente Demo")
                .phone("387-1234567")
                .address("Av. Principal 456")
                .email("cliente@petshop.com")
                .active(true)
                .build());

        // ── PETS ───────────────────────────────────────────────────

        Pet max = petRepository.save(Pet.builder()
                .name("Max")
                .species(PetSpecies.DOG)
                .breed(DogBreed.LABRADOR)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(2021, 5, 10))
                .color("Dorado")
                .weight(28.5)
                .customer(customer)
                .active(true)
                .build());

        Pet luna = petRepository.save(Pet.builder()
                .name("Luna")
                .species(PetSpecies.CAT)
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(2023, 2, 14))
                .color("Negro")
                .weight(4.2)
                .customer(customer)
                .active(true)
                .build());

        Pet piolin = petRepository.save(Pet.builder()
                .name("Piolín")
                .species(PetSpecies.BIRD)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(2024, 1, 20))
                .color("Amarillo")
                .weight(0.15)
                .customer(customer)
                .active(true)
                .build());

        // ── PRODUCTS ──────────────────────────────────────────────

        productRepository.saveAll(List.of(
                Product.builder()
                        .name("Dog Chow Adulto x 20kg")
                        .description("Alimento balanceado para perros adultos")
                        .price(18500.0)
                        .stock(50)
                        .category(ProductCategory.FOOD)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Royal Canin Gato x 10kg")
                        .description("Alimento premium para gatos")
                        .price(22000.0)
                        .stock(30)
                        .category(ProductCategory.FOOD)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Hueso de goma resistente")
                        .description("Juguete mordedor para perros medianos")
                        .price(2500.0)
                        .stock(40)
                        .category(ProductCategory.TOYS)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Rascador para gatos")
                        .description("Rascador con plataforma y pelota colgante")
                        .price(8500.0)
                        .stock(15)
                        .category(ProductCategory.TOYS)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Shampoo neutro para perros x 500ml")
                        .description("Shampoo suave pH balanceado")
                        .price(3200.0)
                        .stock(25)
                        .category(ProductCategory.HYGIENE)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Collar ajustable reflectivo")
                        .description("Collar con tira reflectante, talla M")
                        .price(4500.0)
                        .stock(20)
                        .category(ProductCategory.ACCESSORIES)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Antipulgas Nexgard x 3")
                        .description("Comprimido masticable para perros hasta 25kg")
                        .price(15000.0)
                        .stock(10)
                        .category(ProductCategory.MEDICINE)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Correa retráctil 5m")
                        .description("Correa automática con freno, hasta 25kg")
                        .price(6800.0)
                        .stock(18)
                        .category(ProductCategory.ACCESSORIES)
                        .active(true)
                        .build()
        ));

        // ── APPOINTMENT ───────────────────────────────────────────

        appointmentRepository.save(Appointment.builder()
                .customer(customer)
                .pet(max)
                .dateTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                .reason("Vacunación antirrábica anual")
                .status(AppointmentStatus.CONFIRMED)
                .build());

        System.out.println("✅ Datos iniciales cargados correctamente.");
        System.out.println("   ├─ Admin:    admin@petshop.com / Admin123!");
        System.out.println("   ├─ Veterinario: vet@petshop.com / Vet123!");
        System.out.println("   └─ Cliente:  cliente@petshop.com / Cliente123!");
    }
}
