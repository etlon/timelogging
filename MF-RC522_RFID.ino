#include <SPI.h>
#include <MFRC522.h>
#include <ESP8266WiFi.h>

const char* ssid = "";
const char* password = "";

 
#define SS_PIN D8
#define RST_PIN D3
 
MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class
MFRC522::MIFARE_Key key;
 
void setup(void){
SPI.begin();
rfid.PCD_Init();
 
Serial.begin(115200);

WiFi.begin(ssid, password);

while(WiFi.status() != WL_CONNECTED) {
  delay(1000);
  Serial.print(".");
}
Serial.print("Connected!");
 
}
 
void loop(void){
handleRFID();
}
 
void handleRFID() {
if (!rfid.PICC_IsNewCardPresent()) return;
if (!rfid.PICC_ReadCardSerial()) return;
WiFiClient client;
if (!client.connect("192.168.2.99", 1000)) {
  Serial.print("X");
  return;
}
client.println(printHex(rfid.uid.uidByte, rfid.uid.size));

Serial.println(printHex(rfid.uid.uidByte, rfid.uid.size));
 
rfid.PICC_HaltA();
rfid.PCD_StopCrypto1();
}
 
String printHex(byte *buffer, byte bufferSize) {
String id = "";
for (byte i = 0; i < bufferSize; i++) {
id += buffer[i] < 0x10 ? "0" : "";
id += String(buffer[i], HEX);
}
return id;
}
