package agents;

// Exemple de Bitxo
public class Bitxo1 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    final double dist = 90; //camina v1 a 80
    final double distL = 30;
    int repetir;
    int repetirC;
    int gana; //temps sense menjar
    int recarrega; //temps entre dispars

    Estat estat;

    public Bitxo1(Agents pare) {
        super(pare, "Typhoon", "imatges/robotank1.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(6, 5, 700, 30, 23, 5, 2);
        System.out.println("Cost total:" + cost);

        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        repetirC = 0;
        gana = 0;
        recarrega = 0;
    }

    private void camina() {
        if ((estat.distanciaVisors[CENTRAL] < dist) && (estat.objecteVisor[CENTRAL] == PARET)) {
            if (estat.distanciaVisors[DRETA] < estat.distanciaVisors[ESQUERRA]) {
                gira(10);
            } else if (estat.distanciaVisors[DRETA] > estat.distanciaVisors[ESQUERRA]) {
                gira(350);
            }
        } else if ((estat.distanciaVisors[DRETA] < distL) && (estat.objecteVisor[DRETA] == PARET)) {
            gira(5);
        } else if ((estat.distanciaVisors[ESQUERRA] < distL) && (estat.objecteVisor[ESQUERRA] == PARET)) {
            gira(355);
        }

        if (estat.enCollisio == true) {
            if (repetir > 20) {
                hyperespai();
                repetir = 0;
            } else {
                enrere();
                repetirC = 10;
                repetir += 5;
            }
        }

        System.out.println(repetir);
        if (repetir == 0) {
            atura();
            endavant();
            System.out.println("endevant");
        } else {
            repetir--;
        }
    }

    private boolean cerca() {
        int min = Integer.MAX_VALUE;
        int proper = -1;
        for (int i = 0; i < estat.numObjectes; i++) {
            if (((estat.objectes[i].agafaSector() == 2) || (estat.objectes[i].agafaSector() == 3))) {
                if (recursInteresant(estat.objectes[i])) {
                    if (estat.objectes[i].agafaDistancia() < min) {
                        min = estat.objectes[i].agafaDistancia();
                        proper = i;
                    }
                }
            }
        }
        System.out.println(repetirC);
        if (proper != -1) {
            if (repetirC == 0) {
                mira(estat.objectes[proper]);
                System.out.println("Found it");
            } else {
                repetirC--;
            }
            gana = 0;
            return true;
        }
        return false;
    }

    private boolean recoleccio() {
        if (!cerca()) {
            gana++;
            System.out.println("Gana: " + gana);
            if (gana > 30) {
                if (gana > 39) {
                    gana = 0;
                }
                atura();
                gira(36);
                return true;
            }
        }
        return false;
    }

    private boolean recursInteresant(Objecte objecte) {
        if (objecte.agafaTipus() >= 100) {
            if (objecte.agafaTipus() == (estat.id + 100)) {
                return true;
            } else {
                atac(objecte);
                return false;
            }
        } else if (objecte.agafaTipus() == Estat.ESCUT) {
            return (estat.escuts < 3);
        } else if (objecte.agafaTipus() == Estat.AGENT) {
            atac(objecte);
            return false;
        }
        return false;
    }

    private void defensa() {
        if (estat.llançamentEnemicDetectat && (estat.distanciaLlançamentEnemic < 50) && (!estat.escutActivat)) {
            if (estat.escuts > 0) {
                activaEscut();
            } else {
                hyperespai();
            }
        }
    }

    private void atac(Objecte enemic) {
        if (recarrega <= 0) {
            if (enemic.agafaTipus() >= 100) {
                if (enemic.agafaDistancia() <= 400) {
                    mira(enemic);
                    llança();
                    recarrega = 20;
                }
            } else {
                if (estat.recursosAgafats > 3) {
                    mira(enemic);
                    if (enemic.agafaDistancia() <= 400) {
                        llança();
                    }
                    recarrega = 20;
                }
            }
        }
    }

    @Override
    public void avaluaComportament() {
        estat = estatCombat();
        defensa();
        if (!recoleccio()) {
            camina();
        }
        recarrega--;
    }

}

/*
COSES A FER
    -Si se bloqueja envant y enrere tirar hiperespai. // DONE
    -Sistema de combat // DONE
        -Sistema d'atac. // DONE 
    -Sistema Sonar // NOPE
    -Arreglar obsesio recolectar objectes disparar // DONE 
    -Detectar escuts enemics // NO POSIBLE
    -Controlar cadencia de tir // DONE 
 */
