/*--------------------------------------------------------------------------------------
 *                             
 *                             PROJET ARDUINO FAB-LAB
 *                      (OUVRIER-BUFFET Lucie, RUFFIOT Mégane)
 * 
 * -------------------------------------------------------------------------------------
 */


// Librairie Arduino requise pour communiquer avec des composants sur interface I2C
#include "Wire.h"

// Inclusion d'une bibliothèque permettant de communiquer avec le capteur accélérométrique
// et gyroscopique 3 axes sur interface I2C
// Télécharger à l'adresse http://www.3sigma.fr/telechargements/I2Cdev.zip
// et décompresser dans le sous-répertoire « libraries » de votre installation Arduino
#include "I2Cdev.h"

// Inclusion d'une bibliothèque intègrant de nombreuses fonctions permettant d'accéder aux
// informations mesurées par le MPU6050, qui est le le capteur accélérométrique et
// gyroscopique 3 axes intégré à Geeros.
// Télécharger à l'adresse http://www.3sigma.fr/telechargements/MPU6050.zip
// et décompresser dans le sous-répertoire « libraries » de votre installation Arduino
#include "MPU6050_6Axis_MotionApps20.h"

#include <Mouse.h>

// Déclaration de l'objet "capteur accélérométrique et gyroscopique"
MPU6050 mpu;

uint16_t packetSize;    // taille de paquet DMP attendu (taille par défaut: 42 octets)
uint16_t fifoCount;     // compte de tous les octets actuellement dans la FIFO
uint8_t fifoBuffer[64]; // buffer de stockage de la FIFO

Quaternion q;           // [w, x, y, z]         quaternion
VectorFloat gravity;    // [x, y, z]            vecteur de pesanteur
float euler[3];         // [psi, theta, phi]    angles d'Euler angle
float ypr[3];           // [yaw, pitch, roll]   lacet/tangage/roulis
int axe_x, axe_y, axe_z;            // angles courants
int val_prec[3];                    // valeur des angles de l'iteration precedente
int ecart[3] = {0, 0, 0};           // ecart entre deux angles succesifs
int angle_prec_x = 0;               // derniere valeur de l'angle x (pas de mvt depuis)
int angle_prec_y = 0;               // derniere valeur de l'angle y (pas de mvt depuis)
int decc_x = 1;                     // constante de decceleration sur x
int decc_y = 1;                     // constante de decceleration sur y
int count = 0;                      // detection de la premiere iteration
int dilat_x;                    // dilatation du mouvement selon l'axe x (du gyro)
int dilat_y;                    // dilatation du mouvement selon l'axe y (du gyro)
int width;                          // Largeur de l'ecran
int height;                         // Hauteur de l'ecran
float fitts_x = 1;
float fitts_y = 1;


// Fonction de recalibration
void recalibrate() {
  Serial.println(0);
  for (int i = 0 ; i < 10 ; i++) {
    Mouse.move(-100, -100, 0);
  }
  for (int i = 0 ; i < 4 ; i++) {
    Mouse.move(80, 0, 0); 
  }
  for (int i = 0 ; i < 2 ; i++) {
    Mouse.move(0, 100, 0); 
  }
}

// Initialisations
void setup(void) {
  
  // Liaison série.
  // ATTENTION: ne pas modifier la vitesse de transmission de la liaison série,
  Serial.begin(57600);
  Serial.flush();
       
  // Initialisation du bus I2C
  Wire.begin(0x20);

  // Initialisation du capteur accélérométrique et gyroscopique
  Serial.println("Initialisation de l'IMU...");
  mpu.initialize();
 
  // Chargement et configuration du DMP
  int devStatus = mpu.dmpInitialize();
 
  if (devStatus == 0) {
    // Activation du DMP
    Serial.println(F("Activation du DMP..."));
    mpu.setDMPEnabled(true);
   
    // Acquisition de la taille de paquet DMP pour comparaison ultérieure
    packetSize = mpu.dmpGetFIFOPacketSize();
 
  } else {
    // ERREUR!
    // 1 = échec de chargement initial de la mémoire
    // 2 = échec des mises à jour de configuration du DMP
    Serial.print(F("Echec d'initialisation du DMP (code "));
    Serial.print(devStatus);
    Serial.println(F(")"));
  }
 
  // Test de la connexion
  Serial.println("Test de la connexion...");
  Serial.println(mpu.testConnection() ? "Connexion MPU6050 réussie" : "Connexion MPU6050 échouée");

  // On attend la taille de l'écran envoyée par le programme Java 
  while(!Serial.available()){
  }
  byte upper = Serial.read();
  while(!Serial.available()){
  }
  byte lower = Serial.read();
  width = (upper<<8) | lower; //Reassemble the number
  while(!Serial.available()){
  }
  upper = Serial.read();
  while(!Serial.available()){
  }
  lower = Serial.read();
  height = (upper<<8) | lower; //Reassemble the number

  //Calcul des dilatations à partir de la taille de l'ecran
  dilat_y = (5 * width)/1366;
  dilat_x = (1 * height)/768; 
}

// Boucle principale
void loop() {

    // Acquisition du compte actuel de la FIFO
    fifoCount = mpu.getFIFOCount();
   
    // Surveillance d'overflow (qui ne devrait jamais se produire sauf si le code est inefficace)
    if (fifoCount == 1024) {
      // Reset pour pouvoir continuer proprement
      mpu.resetFIFO();
      Serial.println(F("FIFO overflow!"));
    } 
    else {
      // Attente (a priori tres courte) pour des donnees de longueur correcte
      while (fifoCount < packetSize) {
        fifoCount = mpu.getFIFOCount();
      }
 
      // Lecture d'un paquet dans la FIFO
      mpu.getFIFOBytes(fifoBuffer, packetSize);
     
      // Mise à jour du compte de la FIFO dans le cas ou il y a plus d'un paquet disponible
      // (ce qui permet d'en lire immediatement plus sans attendre une interruption)
      fifoCount -= packetSize;  
 
      // Détermination des angles d'Euler en degrés
      mpu.dmpGetQuaternion(&q, fifoBuffer);
      mpu.dmpGetGravity(&gravity, &q);
      mpu.dmpGetYawPitchRoll(ypr, &q, &gravity);

      // Calcul des angles en degré    
      axe_z = ypr[0] * 180/M_PI;
      axe_y = -ypr[1] * 1800 * 2 /M_PI;
      axe_x = ypr[2] * 1800 * 2 /M_PI;      

    
      // Cas de demarrage : initialisation des valeurs d'ecart 
      if (count == 0) {
        count++;
        val_prec[0] = axe_z;
        val_prec[1] = axe_y;
        val_prec[2] = axe_x;
        angle_prec_x = axe_x;
        angle_prec_y = axe_y;
      }
      
      // MAJ des écarts pour x et y
//      ecart[0] = val_prec[0] - axe_z;
//      ecart[1] = val_prec[1] - axe_y;
//      ecart[2] = val_prec[2] - axe_x;
      ecart[0] = val_prec[0] - axe_z;
      ecart[1] = fitts_y * (val_prec[1] - axe_y);
      ecart[2] = fitts_x * (val_prec[2] - axe_x);
 
      // MAJ des valeurs 
      val_prec[0] = axe_z;
      val_prec[1] = axe_y;
      val_prec[2] = axe_x;

      // Gestion loi de Fitts
      if ((mpu.getRotationX() < 0) && (ecart[2] > 0)) {
        fitts_x = 0.7;
      } else if ((mpu.getRotationX() > 0) && (ecart[2] < 0)) {
        fitts_x = 0.7;
      } else {
        fitts_x = 1;
      }
      if ((mpu.getRotationY() < 0) && (ecart[1] > 0)) {
        fitts_y = 0.7;
      } else if ((mpu.getRotationY() > 0) && (ecart[1] < 0)) {
        fitts_y = 0.7;
      } else {
        fitts_y = 1;
      }
      
      // Deplacement de y (gyro) - Axe x sur l'ecran
      if (abs(angle_prec_y - axe_y) > 3) {      
        angle_prec_y = axe_y;
        Mouse.move(ecart[1] * dilat_y, 0, 0);
      }
      // Deplacement de x (gyro) - Axe y sur l'ecran
      if (abs(angle_prec_x - axe_x) > 3) {
        angle_prec_x = axe_x;
        Mouse.move( 0, ecart[2] * dilat_x, 0);
      }   
    }
    // Detection de calibration
    if (mpu.getRotationY() > 1000) {
      //recalibrate();
      Serial.println(2);
      delay(2000);
    }
    
    // Detection de blocage
    if (mpu.getRotationX() > 1000) {
      Serial.println(3);
      delay(2000);
    }

}


