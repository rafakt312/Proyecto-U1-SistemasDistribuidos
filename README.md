# 📌 Proyecto 1 - Morfología Matemática (Erosión y Dilatación)

Este proyecto implementa operaciones de **morfología matemática** en imágenes a color (**PNG, RGB**) utilizando **Java**.  
Incluye dos versiones del algoritmo:

- **Secuencial**: recorre la imagen píxel a píxel.
- **Paralela**: divide la imagen en submatrices (tiles) y procesa con múltiples hilos.

---

## ⚙️ Requisitos

- **Java 17+**
- **Maven 3.6+**

---

## 🚀 Compilación

Desde la carpeta raíz del proyecto:

```bash
mvn clean package
```

Esto genera el archivo ejecutable en:

```
target/morfologia-jar-with-dependencies.jar
```

---

## ▶️ Ejecución

### 1. **Modo interactivo**
Si ejecutas el `.jar` sin argumentos, se abre un menú por consola:

```bash
java -jar target/morfologia-jar-with-dependencies.jar
```

Ejemplo de preguntas:

```
Modo [seq/par]:
Operación [erosion/dilatacion]:
Elemento estructurante [1..5]:
Política de borde [ignore/pad]:
Hilos (solo par):
Imagen de entrada (PNG):
Imagen de salida (PNG):
Benchmark? [s/n]:
```

---

### 2. **Modo con argumentos (CLI)**

Puedes pasar las opciones directamente con `--parametro valor`:

```bash
java -jar target/morfologia-jar-with-dependencies.jar   --mode seq   --op erosion   --se 1   --edge ignore   --in input.png   --out salida.png
```

---

## 📋 Parámetros disponibles

| Parámetro      | Valores posibles                         | Descripción |
|----------------|------------------------------------------|-------------|
| `--mode`       | `seq` (secuencial) / `par` (paralelo)    | Modo de ejecución |
| `--op`         | `erosion` / `dilatacion`                | Operación de morfología |
| `--se`         | `1..5`                                  | Elemento estructurante (Cuadrado, Cruz, X, Línea, Diamante) |
| `--edge`       | `ignore` / `pad`                        | Manejo de bordes |
| `--threads`    | número (ej: 4, 8, 16)                   | Hilos a usar en modo paralelo |
| `--in`         | ruta a la imagen de entrada (PNG)        | Imagen a procesar |
| `--out`        | ruta a la imagen de salida (PNG)         | Imagen resultante |
| `--bench`      | bandera opcional                        | Ejecuta 3 veces y entrega tiempo promedio |

---

## 📊 Ejemplos

### Erosión secuencial
```bash
java -jar target/morfologia-jar-with-dependencies.jar   --mode seq   --op erosion   --se 2   --edge ignore   --in lena.png   --out lena_erosion.png
```

### Dilatación paralela con 8 hilos
```bash
java -jar target/morfologia-jar-with-dependencies.jar   --mode par   --op dilatacion   --se 5   --edge pad   --threads 8   --in lena.png   --out lena_dilatacion.png
```

### Benchmark (promedio de 3 ejecuciones)
```bash
java -jar target/morfologia-jar-with-dependencies.jar   --mode par   --op erosion   --se 1   --edge pad   --threads 12   --bench   --in big_image.png   --out big_image_out.png
```

---

## 📖 Notas

- Los tiempos de ejecución **no incluyen lectura/escritura** de archivos, solo el cálculo.
- Para imágenes muy grandes (10.000 x 10.000 píxeles), se recomienda ejecutar con mayor memoria:
  ```bash
  java -Xmx4g -jar target/morfologia-jar-with-dependencies.jar ...
  ```
- El proyecto sigue la arquitectura **MIMD (Flynn)**, ya que múltiples hilos procesan diferentes partes de la imagen de forma independiente.

---

## 👨‍💻 Autores

- Javier Gamboa  
- Rafael Gonzalez  
- Victor Cornejo  
