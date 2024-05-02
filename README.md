# backend-rest-app


# Запуск проекту
- Database:
  База даних PostgreSQL була запущена через докер, всі конфігурації щодо запуску контейнера та image знаходяться у **docker-compose.yml** файлі.
  Дані для підключення до бази даних знаходяться в файлі **application.properties**.
- Application:
  Необхідно запустити клас **CarManagementApplication**, який містить головний метод **main**.
# Опис
- Liquibase:
  Cкрипти для ініціалізації та міграції бази даних знаходяться в каталозі **resources/liquibase**.
- uploading:
  JSON файл для завантаження даних про автомобілі **cars-data.json**, розміщений в **resources/data-upload**.
# Тестові дані
- Крім файлу та його вмісту для тестування ендпоінтів можна використовувати наступні JSON стркутури:
- Додавання автомобіля:
   {
   "model": "Fiesta",
   "year": 2022,
   "color": "Black, Yellow",
   "price": 19530.00,
   "brandId": 7
   }
- Фільтрація автомобілів:
   {
   "brandId": 7,
   "minPrice": 18000.00,
   "maxPrice": 22000.00,
   "page": 0,
   "size": 10
   }
- Додавання бренду:
   {
    "name": "Lamborgini",
    "country": "Italy"
   }
  
