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

    Estat estat;

    public Bitxo1(Agents pare) {
        super(pare, "Speedy", "imatges/robotank1.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(6, 5, 600, 30, 23, 5, 5);
        System.out.println("Cost total:" + cost);

        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        repetirC = 0;
        gana = 0;
    }

    private void camina() {
        if (estat.enCollisio == true) {
            enrere();
            repetirC = 5;
        } else if ((estat.distanciaVisors[ESQUERRA] < dist) && (estat.objecteVisor[ESQUERRA] == PARET)) {
            dreta();
            System.out.println("dreta");
            repetir = 2;
        } else if ((estat.distanciaVisors[DRETA] < dist) && (estat.objecteVisor[DRETA] == PARET)) {
            esquerra();
            System.out.println("esquerra");
            repetir = 2;
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

    private void caminaV2() {
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
                endavant();
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
            //if ((estat.objectes[i].agafaDistancia() < min)) {
            //if ((estat.objectes[i].agafaTipus() == estat.id + 100) && (estat.objectes[i].agafaDistancia() < min)) {
            if ((estat.objectes[i].agafaTipus() != -1) && (estat.objectes[i].agafaDistancia() < min)) {
                min = estat.objectes[i].agafaDistancia();
                proper = i;
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

    @Override
    public void avaluaComportament() {
        estat = estatCombat();
        if (!recoleccio()) {
            caminaV2();
        }

    }

}
