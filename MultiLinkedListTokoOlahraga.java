import java.util.*;

class KategoriNode {
    int id;
    String name;
    String deskripsi;
    KategoriNode next;

    public KategoriNode(int id, String name, String deskripsi) {
        this.id = id;
        this.name = name;
        this.deskripsi = deskripsi;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Des: " + deskripsi;
    }
}

class ProductNode {
    String name;
    double price;
    int stock;
    KategoriNode kategori;
    ProductNode next;

    public ProductNode(String name, double price, int stock, KategoriNode kategori) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.kategori = kategori;
    }

    @Override
    public String toString() {
        String kategoriName = (kategori != null) ? kategori.name : "Tidak ada kategori";
        return name + " ($" + price + ", Stok: " + stock + ", Kategori: " + kategoriName + ")";
    }
}

class TransactionNode {
    Date date;
    String type; // untuk purchase atau rental
    ProductNode productHead; // Barang di transaksi
    Map<String, Integer> quantities;
    Map<String, Double> productPrice;
    Map<String, Boolean> returnedStatus;
    TransactionNode next;
    double payment, paidAmount;
    double change, changeAmount;
    Date returnDate;
    int duration;
    boolean isReturned = false;
    UserNode allUser;

    public TransactionNode(String type) {
        this.date = new Date();
        this.type = type;
        this.quantities = new HashMap<>();
        this.productPrice = new HashMap<>();
        this.returnedStatus = new HashMap<>();
    }

    public double getTotalAmount() {
        double total = 0.0;
        ProductNode current = productHead;

        while (current != null) {
            int quantity = quantities.getOrDefault(current.name, 0);
            double itemPrice = current.price;

            if (type.equals("rental")) {
                itemPrice *= 0.2; // Harga 20% dari harga asli
                itemPrice *= duration;
            }

            total += itemPrice * quantity;
            current = current.next;
        }

        return total;
    }

    public double getProductPrice(String productName) {
        ProductNode current = productHead;

        while (current != null) {
            if (current.name.equals(productName)) {
                return current.price;
            }
            current = current.next;
        }

        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Transaction (" + type + ") on " + date + "\nProducts:\n");
        ProductNode current = productHead;
        while (current != null) {
            int qty = quantities.getOrDefault(current.name, 0);
            sb.append(current.name).append(" x").append(qty).append(" ($").append(current.price * qty).append(")\n");
            current = current.next;
        }
        sb.append("Total: $").append(getTotalAmount());
        return sb.toString();
    }
}

class UserNode {
    String username;
    String password;
    String role;
    TransactionNode transactionHead;
    UserNode next;

    public UserNode(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    @Override
    public String toString() {
        return "Username: " + username + ", Role: " + role;
    }
}

public class MultiLinkedListTokoOlahraga {
    private static UserNode userHead;
    private static ProductNode productHead;
    private static KategoriNode kategoriHead;

    public static void main(String[] args) {
        initializeData();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Toko Olahraga ===");
        boolean running = true;

        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1. Login");
            System.out.println("2. Daftar");
            System.out.println("3. Keluar");
            System.out.print("Pilih menu: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Input tidak valid. Harap masukkan angka.");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    login(scanner);
                    break;
                case 2:
                    register(scanner);
                    break;
                case 3:
                    running = false;
                    System.out.println("Terima kasih telah menggunakan aplikasi Toko Olahraga!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
        scanner.close();
    }

    private static void initializeData() {
        userHead = new UserNode("admin", "1234", "admin");

        kategoriHead = new KategoriNode(1, "Sepatu", "Berbagai macam sepatu olahraga");
        kategoriHead.next = new KategoriNode(2, "Pakaian", "Jersey, kaos, dan lainnya");
        kategoriHead.next.next = new KategoriNode(3, "Perlengkapan", "Bola, raket, dll");

        KategoriNode sepatuCategory = getKategoriByIndex(1);
        KategoriNode pakaianCategory = getKategoriByIndex(2);
        KategoriNode perlengkapanCategory = getKategoriByIndex(3);

        productHead = new ProductNode("Sepatu Olahraga", 500000, 10, sepatuCategory);
        productHead.next = new ProductNode("Bola Basket", 300000, 5, perlengkapanCategory);
        productHead.next.next = new ProductNode("Jersey", 200000, 20, pakaianCategory);
    }

    private static void login(Scanner scanner) {
        System.out.print("Masukkan username: ");
        String username = scanner.nextLine();

        System.out.print("Masukkan password: ");
        String password = scanner.nextLine();

        UserNode user = authenticateUser(username, password);

        if (user != null) {
            System.out.println("Login berhasil! Selamat datang, " + user.username + " (" + user.role + ")");
            if (user.role.equals("admin")) {
                adminMenu(scanner, user);
            } else {
                userMenu(scanner, user);
            }
        } else {
            System.out.println("Login gagal. Username atau password salah.");
        }
    }

    private static void register(Scanner scanner) {
        while (true) {
            System.out.print("Masukkan username baru: ");
            String username = scanner.nextLine();

            if (isUsernameExists(username)) {
                System.out.println("Username sudah ada. Silakan coba username lain.");
                continue;
            }

            System.out.print("Masukkan password baru: ");
            String password = scanner.nextLine();

            UserNode newUser = new UserNode(username, password, "user");
            newUser.next = userHead;
            userHead = newUser;

            System.out.println("Registrasi berhasil! Silakan login dengan akun baru Anda.");
            break;
        }
    }

    private static boolean isUsernameExists(String username) {
        UserNode current = userHead;
        while (current != null) {
            if (current.username.equals(username)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private static UserNode authenticateUser(String username, String password) {
        UserNode current = userHead;
        while (current != null) {
            if (current.username.equals(username) && current.authenticate(password)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    private static void adminMenu(Scanner scanner, UserNode users) {
        boolean running = true;

        while (running) {
            System.out.println("\nMenu Admin:");
            System.out.println("1. Lihat Semua User");
            System.out.println("2. Lihat Semua Kategori");
            System.out.println("3. Lihat Semua Produk");
            System.out.println("4. Lihat Daftar Sewa Produk");
            System.out.println("5. Lihat Semua Transaksi");
            System.out.println("6. Keluar");
            System.out.print("Pilih menu: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Input tidak valid. Harap masukkan angka.");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    viewKategori();
                    menuKategori(scanner);
                    break;
                case 3:
                    viewProducts(false);
                    menuProducts(scanner);
                    break;
                case 4:
                    manageAllRentals(scanner);
                    break;
                case 5:
                    viewAllTransactions();
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void userMenu(Scanner scanner, UserNode user) {
        boolean running = true;

        while (running) {
            System.out.println("\nMenu User:");
            System.out.println("1. Lihat Produk");
            System.out.println("2. Beli Produk");
            System.out.println("3. Sewa Produk");
            System.out.println("4. Daftar Sewa Produk");
            System.out.println("5. Lihat Transaksi Saya");
            System.out.println("6. Keluar");
            System.out.print("Pilih menu: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Input tidak valid. Harap masukkan angka.");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewProducts(false);
                    break;
                case 2:
                    purchaseProducts(scanner, user);
                    break;
                case 3:
                    rentProducts(scanner, user);
                    break;
                case 4:
                    returnRentedProducts(scanner, user);
                    break;
                case 5:
                    viewTransactions(user);
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static KategoriNode getKategoriByIndex(int index) {
        KategoriNode current = kategoriHead;
        int count = 1;
        while (current != null) {
            if (count == index) {
                return current;
            }
            current = current.next;
            count++;
        }
        return null;
    }

    private static boolean isCategoryIdExists(int id) {
        KategoriNode current = kategoriHead;
        while (current != null) {
            if (current.id == id) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private static void addKategori(Scanner scanner) {
        System.out.print("Masukkan ID kategori: ");
        String idInp = scanner.nextLine();

        if (!idInp.matches("\\d+")) {
            System.out.println("ID Kategori harus berupa angka.");
            return;
        }

        int id = Integer.parseInt(idInp);

        if (isCategoryIdExists(id)) {
            System.out.println("ID Kategori sudah ada, Coba yang lain");
            return;
        }

        System.out.print("Masukkan nama kategori: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            System.out.println("Nama Kategori tidak boleh kosong");
            return;
        }

        System.out.print("Masukkan deskripsi kategori: ");
        String description = scanner.nextLine();

        KategoriNode newCategory = new KategoriNode(id, name, description);

        newCategory.next = kategoriHead;
        kategoriHead = newCategory;

        System.out.println("Kategori berhasil ditambahkan.");
    }

    private static boolean isKategoryLinkedToProduct(KategoriNode kategori) {
        ProductNode currentProduct = productHead;
        while (currentProduct != null) {
            if (currentProduct.kategori == kategori) {
                return true;
            }
            currentProduct = currentProduct.next;
        }
        return false;
    }

    private static void editKategori(Scanner scanner) {
        System.out.println("\n=== Edit Kategori ===");
        viewKategori();

        System.out.print("Pilih nomor Kategori yang ingin diedit: ");
        int index = scanner.nextInt();
        scanner.nextLine();

        KategoriNode kategori = getKategoriByIndex(index);
        if (kategori == null) {
            System.out.println("Kategori tidak ditemukan.");
            return;
        }
        if (isKategoryLinkedToProduct(kategori)) {
            System.out.println("Kategori ini terkait dengan produk. Tidak dapat diedit");
            return;
        }

        System.out.print("Masukkan nama baru kategori (kosong untuk tidak mengubah): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            kategori.name = newName;
        }

        System.out.print("Masukkan deskripsi baru kategori (kosong untuk tidak mengubah): ");
        String newDescription = scanner.nextLine();
        if (!newDescription.isEmpty()) {
            kategori.deskripsi = newDescription;
        }

        System.out.println("Kategori berhasil diperbarui.");
    }

    private static void viewKategori() {
        System.out.println("\nDaftar Kategori:");

        if (kategoriHead == null) {
            System.out.println("Tidak ada kategori yang tersedia.");
            return;
        }

        kategoriHead = sortCategoriesById(kategoriHead);
        KategoriNode current = kategoriHead;
        int index = 1;
        while (current != null) {
            System.out.println(index + ". " + current);
            current = current.next;
            index++;
        }
    }

    private static KategoriNode sortCategoriesById(KategoriNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        boolean swapped;
        do {
            swapped = false;
            KategoriNode current = head;
            KategoriNode prev = null;

            while (current != null && current.next != null) {
                if (current.id > current.next.id) {
                    KategoriNode temp = current.next;
                    current.next = temp.next;
                    temp.next = current;

                    if (prev == null) {
                        head = temp;
                    } else {
                        prev.next = temp;
                    }

                    swapped = true;
                }

                prev = current;
                current = current.next;
            }
        } while (swapped);

        return head;
    }

    private static void viewProducts(boolean isRental) {
        System.out.println("\nDaftar Produk:");
        ProductNode current = productHead;
        int index = 1;

        if (current == null) {
            System.out.println("Tidak ada produk yang tersedia.");
            return;
        }

        while (current != null) {
            String kategoriInfo = (current.kategori != null)
                    ? current.kategori.name
                    : "Tidak ada kategori";
            double displayPrice = isRental ? current.price * 0.2 : current.price;

            System.out.printf("%d. Nama: %s, Harga: Rp%,.2f, Stok: %d, Kategori: %s%n",
                    index, current.name, displayPrice, current.stock, kategoriInfo);

            current = current.next;
            index++;
        }
    }

    private static void menuKategori(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\nMenu Kategori:");
            System.out.println("1. Tambah Kategori");
            System.out.println("2. Edit Kategori");
            System.out.println("3. Keluar");
            System.out.print("Pilih menu: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Input tidak valid. Harap masukkan angka.");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addKategori(scanner);
                    break;
                case 2:
                    editKategori(scanner);
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void menuProducts(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\nMenu Produk:");
            System.out.println("1. Tambah Produk");
            System.out.println("2. Tambah Stok Produk");
            System.out.println("3. Edit Produk");
            System.out.println("4. Delete Produk");
            System.out.println("5. Keluar");
            System.out.print("Pilih menu: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Input tidak valid. Harap masukkan angka.");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addProduct(scanner);
                    break;
                case 2:
                    addStokProduct(scanner);
                    break;
                case 3:
                    editProduct(scanner);
                    break;
                case 4:
                    deleteProduct(scanner);
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void addProduct(Scanner scanner) {
        System.out.println("\n=== Tambah Produk Baru ===");

        System.out.print("Masukkan nama produk: ");
        String name = scanner.nextLine();

        System.out.print("Masukkan harga produk: ");
        double price = scanner.nextDouble();

        System.out.print("Masukkan stok produk: ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Pilih Kategori untuk Produk: ");
        viewKategori();

        int kategoriIndex = -1;
        KategoriNode kategori = null;

        while (kategori == null) {
            System.out.print("Masukkan ID Kategori: ");
            if (scanner.hasNextInt()) {
                kategoriIndex = scanner.nextInt();
                scanner.nextLine();
                kategori = getKategoriByIndex(kategoriIndex);

                if (kategori == null) {
                    System.out.println("Kategori tidak ditemukan. Silakan masukkan ID yang valid.");
                }
            } else {
                System.out.println("Input harus berupa angka! Silakan coba lagi.");
                scanner.nextLine();
            }
        }

        ProductNode newProduct = new ProductNode(name, price, stock, kategori);

        newProduct.next = productHead;
        productHead = newProduct;

        System.out.println("Produk berhasil ditambahkan.");

    }

    private static void addStokProduct(Scanner scanner) {
        System.out.println("\n=== Tambah Stok Produk ===");
        viewProducts(false);

        int index = -1;
        ProductNode product = null;

        while (product == null) {
            System.out.print("Pilih nomor produk untuk menambahkan stok: ");
            if (scanner.hasNextInt()) {
                index = scanner.nextInt();
                scanner.nextLine();

                if (index < 1) {
                    System.out.println("Indeks tidak valid. Masukkan nomor produk yang benar.");
                } else {
                    product = getProductByIndex(index);
                    if (product == null) {
                        System.out.println("Produk tidak ditemukan. Masukkan nomor produk yang valid.");
                    }
                }
            } else {
                System.out.println("Input harus berupa angka! Silakan coba lagi.");
                scanner.nextLine();
            }
        }

        System.out.println("Produk terpilih: " + product.name + " (Stok saat ini: " + product.stock + ")");

        int stokTambah = -1;

        while (stokTambah <= 0) {
            System.out.print("Masukkan jumlah stok yang ingin ditambahkan: ");
            if (scanner.hasNextInt()) {
                stokTambah = scanner.nextInt();
                scanner.nextLine();

                if (stokTambah <= 0) {
                    System.out.println("Jumlah stok harus lebih dari 0. Silakan coba lagi.");
                }
            } else {
                System.out.println("Input harus berupa angka! Silakan coba lagi.");
                scanner.nextLine();
            }
        }

        int stokAwal = product.stock;
        product.stock += stokTambah;
        System.out.println("Stok berhasil ditambahkan.");
        System.out.println("Stok awal: " + stokAwal);
        System.out.println("Jumlah ditambahkan: " + stokTambah);
        System.out.println("Stok baru: " + product.stock);
    }

    private static void editProduct(Scanner scanner) {
        System.out.println("\n=== Edit Produk ===");
        viewProducts(false);

        ProductNode product = null;
        int index = -1;

        while (product == null) {
            System.out.print("Pilih nomor produk yang ingin diedit: ");
            if (scanner.hasNextInt()) {
                index = scanner.nextInt();
                scanner.nextLine();

                if (index < 1) {
                    System.out.println("Nomor produk tidak valid. Masukkan nomor produk yang benar.");
                } else {
                    product = getProductByIndex(index);
                    if (product == null) {
                        System.out.println("Produk tidak ditemukan. Masukkan nomor produk yang valid.");
                    }
                }
            } else {
                System.out.println("Input harus berupa angka! Silakan coba lagi.");
                scanner.nextLine();
            }
        }

        System.out.println("Produk yang dipilih: " + product);

        System.out.print("Masukkan nama baru produk (kosong untuk tidak mengubah): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            product.name = name;
        }

        double price = -1;
        while (price < 0) {
            System.out.print("Masukkan harga baru produk (0 untuk tidak mengubah): ");
            if (scanner.hasNextDouble()) {
                price = scanner.nextDouble();
                scanner.nextLine();
                if (price > 0) {
                    product.price = price;
                } else if (price < 0) {
                    System.out.println("Harga tidak boleh negatif. Silakan coba lagi.");
                }
            } else {
                System.out.println("Input harus berupa angka! Silakan coba lagi.");
                scanner.nextLine();
            }
        }

        int stock = -2;
        while (stock < -1) {
            System.out.print("Masukkan stok baru produk (-1 untuk tidak mengubah): ");
            if (scanner.hasNextInt()) {
                stock = scanner.nextInt();
                scanner.nextLine();
                if (stock >= 0) {
                    product.stock = stock;
                } else if (stock == -1) {
                    break;
                } else {
                    System.out.println("Stok tidak boleh kurang dari -1. Silakan coba lagi.");
                }
            } else {
                System.out.println("Input harus berupa angka! Silakan coba lagi.");
                scanner.nextLine();
            }
        }

        int kategoriIndex = -1;
        while (kategoriIndex < 0) {
            System.out.println("Pilih kategori baru untuk produk (0 untuk tidak mengubah):");
            viewKategori();
            System.out.print("Masukkan nomor kategori: ");
            if (scanner.hasNextInt()) {
                kategoriIndex = scanner.nextInt();
                scanner.nextLine();
                if (kategoriIndex == 0) {
                    break;
                } else {
                    KategoriNode kategori = getKategoriByIndex(kategoriIndex);
                    if (kategori != null) {
                        product.kategori = kategori;
                    } else {
                        System.out.println("Kategori tidak valid. Silakan coba lagi.");
                        kategoriIndex = -1;
                    }
                }
            } else {
                System.out.println("Input harus berupa angka! Silakan coba lagi.");
                scanner.nextLine();
            }
        }

        System.out.println("Produk berhasil diperbarui.");
    }

    private static ProductNode getProductByIndex(int index) {
        ProductNode current = productHead;
        int count = 1;
        while (current != null) {
            if (count == index) {
                return current;
            }
            current = current.next;
            count++;
        }
        return null;
    }

    private static ProductNode getProductByName(String productName) {
        ProductNode current = productHead;

        while (current != null) {
            if (current.name.equalsIgnoreCase(productName)) {
                return current;
            }
            current = current.next;
        }

        return null;
    }

    // private static double getProductPriceByName(String productName) {
    // ProductNode current = productHead;
    // while (current != null) {
    // if (current.name.equals(productName)) {
    // return current.price;
    // }
    // current = current.next;
    // }
    // return 0;
    // }

    private static void deleteProduct(Scanner scanner) {
        System.out.println("\n=== Hapus Produk ===");
        viewProducts(false);

        System.out.print("Pilih nomor produk yang ingin dihapus: ");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1) {
            System.out.println("Indeks tidak valid. Masukkan nomor produk yang benar.");
            return;
        }

        ProductNode productToDelete = getProductByIndex(index);
        if (productToDelete == null) {
            System.out.println("Produk tidak ditemukan.");
            return;
        }

        // Validasi apakah produk sedang disewa
        if (isProductRented(productToDelete.name)) {
            System.out.println("Produk sedang disewa oleh pengguna dan tidak dapat dihapus.");
            return;
        }

        if (index == 1) { // Jika produk yang dihapus adalah head
            productHead = productHead.next;
        } else {
            ProductNode current = productHead;
            int count = 1;
            while (current != null && count < index - 1) {
                current = current.next;
                count++;
            }

            if (current != null && current.next != null) {
                current.next = current.next.next;
            } else {
                System.out.println("Produk tidak ditemukan.");
                return;
            }
        }

        System.out.println("Produk berhasil dihapus.");
    }

    private static boolean isProductRented(String productName) {
        UserNode currentUser = userHead;

        while (currentUser != null) {
            TransactionNode currentTransaction = currentUser.transactionHead;

            while (currentTransaction != null) {
                if (currentTransaction.type.equals("rental") && !currentTransaction.isReturned) {
                    if (currentTransaction.quantities.containsKey(productName)) {
                        return true; // Produk sedang disewa
                    }
                }
                currentTransaction = currentTransaction.next;
            }

            currentUser = currentUser.next;
        }

        return false; // Tidak ditemukan dalam transaksi rental
    }

    private static void purchaseProducts(Scanner scanner, UserNode user) {
        TransactionNode transaction = new TransactionNode("purchase");
        ProductNode cartHead = null;
        ProductNode cartTail = null;

        boolean shopping = true;
        double bayar, kembalian;
        while (shopping) {
            viewProducts(false);
            System.out.print("Pilih nomor produk (0 untuk selesai): ");
            int index = scanner.nextInt();
            scanner.nextLine();

            if (index == 0) {
                shopping = false;
            } else {
                ProductNode product = getProductByIndex(index);
                KategoriNode kategori = getKategoriByIndex(index);
                if (product != null && product.stock > 0) {
                    System.out.print("Masukkan jumlah: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();

                    if (quantity > product.stock) {
                        System.out.println("Stok tidak cukup!");
                    } else {
                        ProductNode cartItem = new ProductNode(product.name, product.price, quantity, kategori);
                        if (cartHead == null) {
                            cartHead = cartItem;
                            cartTail = cartItem;
                        } else {
                            cartTail.next = cartItem;
                            cartTail = cartItem;
                        }

                        transaction.quantities.put(product.name, quantity);
                        product.stock -= quantity;
                        System.out.println(product.name + " x" + quantity + " berhasil ditambahkan ke keranjang.");
                    }
                } else {
                    System.out.println("Produk tidak ditemukan atau stok habis.");
                }
            }
        }

        transaction.productHead = cartHead;

        if (cartHead != null) {
            transaction.next = user.transactionHead;
            user.transactionHead = transaction;
            System.out.printf("\nTotal: Rp%,.2f%n", transaction.getTotalAmount());

            while (true) {
                System.out.print("Masukkan Pembayaran: ");
                bayar = scanner.nextDouble();
                scanner.nextLine();

                if (bayar < transaction.getTotalAmount()) {
                    System.out.printf("Pembayaran Masih Kurang!");
                    System.out.println("Silakan masukkan pembayaran lagi.");
                } else {
                    kembalian = bayar - transaction.getTotalAmount();
                    System.out.printf("Kembalian: Rp%,.2f%n", kembalian);
                    System.out.println("Transaksi Berhasil - Pembayaran Berhasil!");
                    transaction.paidAmount = bayar;
                    transaction.change = kembalian;
                    break;
                }
            }
        } else {
            System.out.println("Keranjang kosong, transaksi dibatalkan.");
        }
    }

    private static void rentProducts(Scanner scanner, UserNode user) {
        TransactionNode transaction = new TransactionNode("rental");
        ProductNode cartHead = null;
        ProductNode cartTail = null;

        boolean renting = true;
        double bayar, kembalian;
        int days = 0;

        while (renting) {
            viewProducts(true);
            System.out.print("Pilih nomor produk untuk disewa (0 untuk selesai): ");
            int index = scanner.nextInt();
            scanner.nextLine();

            if (index == 0) {
                renting = false;
            } else {
                ProductNode product = getProductByIndex(index);
                KategoriNode kategori = getKategoriByIndex(index);
                if (product != null && product.stock > 0) {
                    System.out.print("Masukkan jumlah: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Masukkan durasi sewa (dalam hari): ");
                    days = scanner.nextInt();
                    scanner.nextLine();

                    if (quantity > product.stock) {
                        System.out.println("Stok tidak cukup!");
                    } else {
                        double rentalPricePerDay = product.price * 0.2;
                        double totalRentalPrice = rentalPricePerDay * days * quantity;

                        ProductNode cartItem = new ProductNode(product.name, product.price, quantity, kategori);
                        if (cartHead == null) {
                            cartHead = cartItem;
                            cartTail = cartItem;
                        } else {
                            cartTail.next = cartItem;
                            cartTail = cartItem;
                        }

                        transaction.productPrice.put(product.name, product.price);

                        transaction.quantities.put(product.name, quantity);
                        product.stock -= quantity;
                        System.out.printf(
                                "%s x%d selama %d hari berhasil ditambahkan ke keranjang sewa (Harga: Rp%,.2f).%n",
                                product.name, quantity, days, totalRentalPrice);
                    }
                } else {
                    System.out.println("Produk tidak ditemukan atau stok habis.");
                }
            }
        }

        transaction.productHead = cartHead;
        transaction.duration = days;

        if (cartHead != null) {
            transaction.next = user.transactionHead;
            user.transactionHead = transaction;
            System.out.printf("Total Sewa: Rp%,.2f%n", transaction.getTotalAmount());
            System.out.print("Masukkan Pembayaran: ");
            bayar = scanner.nextDouble();
            scanner.nextLine();

            while (bayar < transaction.getTotalAmount()) {
                System.out.println("Pembayaran kurang, silakan masukkan kembali:");
                System.out.printf("Kurang: Rp%,.2f%n", transaction.getTotalAmount() - bayar);
                bayar += scanner.nextDouble();
                scanner.nextLine();
            }

            kembalian = bayar - transaction.getTotalAmount();
            transaction.paidAmount = bayar;
            transaction.change = kembalian;

            System.out.printf("Kembalian: Rp%,.2f%n", kembalian);
            System.out.println("Transaksi Berhasil - Pembayaran Berhasil!");
        } else {
            System.out.println("Keranjang kosong, penyewaan dibatalkan.");
        }
    }

    private static void returnRentedProducts(Scanner scanner, UserNode user) {
        System.out.println("\nDaftar Penyewaan:");
        TransactionNode currentTransaction = user.transactionHead;
        List<String> rentedProducts = new ArrayList<>();
        Map<String, TransactionNode> productToTransactionMap = new HashMap<>();
        boolean hasRentals = false;

        // mengiterasi untuk mengumpulkan produk yang disewa
        while (currentTransaction != null) {
            if (currentTransaction.type.equals("rental") && !currentTransaction.isReturned) {
                for (Map.Entry<String, Integer> entry : currentTransaction.quantities.entrySet()) {
                    String productName = entry.getKey();
                    if (!currentTransaction.returnedStatus.getOrDefault(productName, false)) {
                        rentedProducts.add(productName);
                        productToTransactionMap.put(productName, currentTransaction);
                        hasRentals = true;
                    }
                }
            }
            currentTransaction = currentTransaction.next;
        }

        if (!hasRentals) {
            System.out.println("Tidak ada barang yang sedang disewa.");
            return;
        }

        for (int i = 0; i < rentedProducts.size(); i++) {
            String productName = rentedProducts.get(i);
            int quantity = productToTransactionMap.get(productName).quantities.get(productName);
            System.out.printf("%d. %s x%d%n", i + 1, productName, quantity);
        }

        System.out.print("Pilih nomor produk yang ingin dikembalikan: ");
        int productIndex = scanner.nextInt();
        scanner.nextLine();

        if (productIndex < 1 || productIndex > rentedProducts.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        String selectedProductName = rentedProducts.get(productIndex - 1);
        TransactionNode transaction = productToTransactionMap.get(selectedProductName);

        if (transaction != null) {
            int quantity = transaction.quantities.get(selectedProductName);

            // mengembalikan stok produk
            ProductNode product = getProductByName(selectedProductName);
            if (product != null) {
                product.stock += quantity;
            }

            // merubah status barang
            transaction.returnedStatus.put(selectedProductName, true);
            transaction.returnDate = new Date();

            System.out.printf("%s x%d berhasil dikembalikan.%n", selectedProductName, quantity);
        } else {
            System.out.println("Produk tidak ditemukan dalam penyewaan Anda.");
        }
    }

    private static void manageAllRentals(Scanner scanner) {
        System.out.println("\nDaftar Semua Penyewaan:");
        boolean hasRentals = false;

        UserNode currentUser = userHead; // Iterasi melalui semua user
        while (currentUser != null) {
            TransactionNode currentTransaction = currentUser.transactionHead; // Iterasi transaksi per user
            while (currentTransaction != null) {
                if ("rental".equals(currentTransaction.type) && !currentTransaction.isReturned) {
                    System.out.printf("User: %s\n", currentUser.username);
                    for (Map.Entry<String, Integer> entry : currentTransaction.quantities.entrySet()) {
                        System.out.printf("   - %s x%d\n", entry.getKey(), entry.getValue());
                    }
                    hasRentals = true;
                }
                currentTransaction = currentTransaction.next;
            }
            currentUser = currentUser.next;
        }

        if (!hasRentals) {
            System.out.println("Tidak ada barang yang sedang disewa.");
        }
    }

    private static void viewAllUsers() {
        System.out.println("\nDaftar User:");
        UserNode current = userHead;
        while (current != null) {
            System.out.println("- " + current);
            current = current.next;
        }
    }

    private static void viewTransactions(UserNode user) {
        System.out.println("\nDaftar Transaksi Anda:");
        TransactionNode current = user.transactionHead;
        while (current != null) {
            final TransactionNode transaction = current;
            System.out.printf("Jenis Transaksi: %s\n", current.type);

            current.quantities.forEach((productName, quantity) -> {
                double productPrice = transaction.productPrice.getOrDefault(productName, 0.0);
                double productTotal = productPrice * quantity;
                boolean isReturned = transaction.returnedStatus.getOrDefault(productName, false);
                String status = isReturned ? "Sudah Dikembalikan" : "Belum Dikembalikan";
                System.out.printf("Produk: %s, Jumlah: %d, Harga Satuan: Rp%,.2f, Total: Rp%,.2f, Status: %s%n",
                        productName, quantity, productPrice, productTotal, status);
            });

            double totalAmount = current.getTotalAmount();
            System.out.printf("Total: Rp%,.2f, Dibayar: Rp%,.2f, Kembalian: Rp%,.2f\n",
                    totalAmount, current.paidAmount, current.change);

            // if (current.type.equals("rental")) {
            // String status = current.isReturned ? "Sudah Dikembalikan" : "Belum
            // Dikembalikan";
            // System.out.println("Status: " + status);
            // if (current.returnDate != null) {
            // System.out.println("Tanggal Pengembalian: " + current.returnDate);
            // }
            // }

            current = current.next;
            System.out.println("________________________________________");
            System.out.println();
        }
    }

    private static void viewAllTransactions() {
        System.out.println("\nDaftar Semua Transaksi:");
        UserNode currentUser = userHead;

        while (currentUser != null) {
            System.out.println("Transaksi untuk: " + currentUser.username);
            TransactionNode currentTransaction = currentUser.transactionHead;

            while (currentTransaction != null) {
                final TransactionNode transaction = currentTransaction;
                System.out.println("====================================");
                System.out.println("Jenis Transaksi: " + currentTransaction.type);
                System.out.println("Tanggal Transaksi: " + currentTransaction.date);

                // salinan sementara untuk digunakan dalam lambda
                Map<String, Integer> quantities = currentTransaction.quantities;
                Map<String, Double> productPrices = currentTransaction.productPrice;

                quantities.forEach((productName, quantity) -> {
                    double productPrice = productPrices.getOrDefault(productName, 0.0);
                    double total = productPrice * quantity;
                    boolean isReturned = transaction.returnedStatus.getOrDefault(productName, false);
                    String status = isReturned ? "Sudah Dikembalikan" : "Belum Dikembalikan";

                    System.out.printf("Produk: %s, Jumlah: %d, Harga Satuan: Rp%,.2f, Total: Rp%,.2f, Status: %s%n",
                            productName, quantity, productPrice, total, status);
                });

                System.out.printf("Total Transaksi: Rp%,.2f%n", currentTransaction.getTotalAmount());

                if (currentTransaction.type.equals("purchase")) {
                    System.out.printf("Dibayar: Rp%,.2f, Kembalian: Rp%,.2f%n",
                            currentTransaction.paidAmount, currentTransaction.change);
                } else if (currentTransaction.type.equals("rental")) {
                    String status = currentTransaction.isReturned ? "Sudah Dikembalikan" : "Belum Dikembalikan";
                    System.out.printf("Durasi Sewa: %d hari%n", currentTransaction.duration);
                    System.out.println("Status: " + status);
                    if (currentTransaction.returnDate != null) {
                        System.out.println("Tanggal Pengembalian: " + currentTransaction.returnDate);
                    }
                }

                System.out.println("====================================");
                currentTransaction = currentTransaction.next;
            }
            currentUser = currentUser.next;
            System.out.println();
        }
    }
}