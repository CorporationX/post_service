// Функция для проваленного теста
function fail(message) {
  throw new Error(message);
}

// Пример провального теста
function exampleFailingTest() {
  const expected = 6;
  const actual = 3 + 2;

  if (actual !== expected) {
    fail(`Тест провален: ожидалось ${expected}, получено ${actual}`);
  }
}

// Вызываем провальный тест
try {
  exampleFailingTest();
} catch (error) {
  console.error('Ошибка в провальном тесте:', error.message);
}
