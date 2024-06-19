# Brubank Challenge

## Se uso:
 - Jetpack Compose
 - MVVM
 - Navigation
 - Hilt
 - Room
 - Retrofit
 - Coil
 - Palette
 - Safe Args
 - Mockito
 - Gradle version catalog

## Cómo está diseñada:
### Vistas
 - Es una app de una sola actividad con 3 fragmentos: Home, Detail, Search. La idea de usar fragmentos fue para imitar la adopción de Compose en una app con vistas clásicas de XML. También para aprovechar las librerías de navegación y Safe Args que nos dan type safety al navegar entre destinos.
 - Seguí el patrón MVVM con observables en los ViewModels.
 - La lista de favoritos no se muestra si no tenemos favoritos guardados en la base de datos así evitamos mostrar el estado vacío al instalar la app.
 - Si falla una conexión a la API, se muestra un mensaje de error en un Snackbar.
 - Se usa paginación para la las recomendaciones del Home, para el Search no lo vi necesario ya que no retorna muchas entradas pero de requerirlo podriamos hacer algo similar a lo del Home.
 - Se agrego una barra de progreso para indicar que esta cargando nuevas recomendaciones.

### Datos
 - Dentro de la capa de datos tenemos Room para la base de datos local y Retrofit para el servicio de la API.
 - Cada tipo de datos tiene su DTO correspondiente y son mapeados a un modelo de UI, ya que puede que no siempre queramos exponer todos los campos a las vistas.
 - Se inyectan los dispatchers en el repo para no hardcodearlos y que sea más flexible su uso para tests o por algún ajuste a futuro.
 - Guardamos los generos en una base de datos local, puede que sea overkill si son datos que no cambian mucho y alternativas como SharedPref o DataStore sean suficiente.
 - Al hacer el fetch de las peliculas revisamos si tenemos el genero en la base de datos y si no lo tenemos, se busca en la API y guardamos en la base de datos.
 - Uso de async/await para buscar la data en paralelo y agilizar el proceso.

### ViewModels
 - Cada fragmento tiene su ViewModel donde se encuentra la lógica asociada a cada vista.
 - En DetailViewModel accedo a los args desde el SavedStateHandle. Se podría usar esto para un estado inicial y luego hacer otro fetch para complementar la información necesaria.

## Testing:
Se uso Mockito para las pruebas unitarias de los ViewModels:
- HomeViewModelTest
- SearchViewModelTest
- DetailViewModelTest

## Cosas a mejorar:
 - Estado de loading: mostrar un progress bar cuando se cargan los datos y skeleton en las vistas.
 - Offline First: sería interesante ver si es viable esto para poder seguir usando la app aunque estemos sin internet.
 - Sistema de diseño UI: temas, colores, tipografía, etc.
 - Modularizar componentes: va de la mano con el anterior. Deberíamos mover componentes que vamos a reutilizar en un archivo de componentes con un estilo de diseño para tener consistencia y reutilizarlos en varias partes de la app.
 - Mejorar rendimiento: note que en algunas partes estamos recomponiendo las vistas innecesariamente.

## Nota:
Dejé la API KEY quemada en la app para que les sea más fácil correr la app. La del PDF no me funcionaba así que generé una nueva.

Idealmente la key va escondida o la cargamos al inicio de la app desde back para evitar exponerla direcamente.

## Screenshots:
<img src="https://github.com/puntogris/brubank-challenge/blob/main/screenshots/1.png" width=250><img src="https://github.com/puntogris/brubank-challenge/blob/main/screenshots/2.png" width=250><img src="https://github.com/puntogris/brubank-challenge/blob/main/screenshots/3.png" width=250><img src="https://github.com/puntogris/brubank-challenge/blob/main/screenshots/4.png" width=250><img src="https://github.com/puntogris/brubank-challenge/blob/main/screenshots/5.gif" width=250><img src="https://github.com/puntogris/brubank-challenge/blob/main/screenshots/6.gif" width=250>