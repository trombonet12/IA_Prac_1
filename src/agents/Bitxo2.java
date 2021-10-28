package agents;

// Exemple de Bitxo
public class Bitxo2 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;

    //Torna el valor de totes les variables d'entorn.
    Estat estat;
    int minim, minim2;
    int repetirMirar, repetirMirar2;
    boolean seguimColisio;
    int posicioArray;
    int posicioArray2;
    int tencGana;
    int noVeigRes;
    boolean faigVolta;

    public Bitxo2(Agents pare) {
        super(pare, "Perry", "imatges/robotank3.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(5, 4, 650, 40, 26, 5, 4);
        System.out.println("Cost total:" + cost);

        // Inicialització de variables que utilitzaré al meu comportament
        minim = Integer.MAX_VALUE;
        minim2 = Integer.MAX_VALUE;
        repetirMirar = 2;
        repetirMirar2 = 2;
        tencGana = 45;
        seguimColisio = false;
        noVeigRes = 0;
        faigVolta = false;
    }

    @Override
    public void avaluaComportament() {
        estat = estatCombat();
        disparEnemicDetectat();
        recolectar();
        if(!faigVolta){
            System.out.println("CAMINAM");
            faigVolta = false;
            camina(50);
        }
        

    }

    //MÈTODES RELACIONATS AMB EL MOVIMENT.
    private void camina(int distancia) {
        if (estat.enCollisio == true) {
            if (!seguimColisio) {
                System.out.println("COLISIO");
                enrere();
                seguimColisio = true;
            } else {
                System.out.println("doble colisio");
                endavant();
                seguimColisio = false;
            }
        } else {
            seguimColisio = false;
        }
        if (hiHaParet(distancia)) {
            System.out.println("hihaParet, no sabem on");
            sortida();
        } else {
            System.out.println("No hi ha paret, endavant");
            endavant();
        }
    }

    private boolean hiHaParet(int distancia) {

        return hiHaParentPerVisor(CENTRAL, distancia) || hiHaParentPerVisor(DRETA, distancia) || hiHaParentPerVisor(ESQUERRA, distancia);
    }

    private boolean hiHaParentPerVisor(int visor, int distancia) {
        return estat.distanciaVisors[visor] <= distancia && estat.objecteVisor[visor] == PARET;
    }

    private void sortida() {

        if (estat.distanciaVisors[DRETA] < estat.distanciaVisors[ESQUERRA]) {
            System.out.println("Cap a l'esquerra");
            gira(10);
            endavant();
        } else if (estat.distanciaVisors[ESQUERRA] < estat.distanciaVisors[DRETA]) {
            System.out.println("Cap a la dreta");
            gira(-10);
            endavant();
        }
    }

    //MÈTODES RELACIONATS AMB LA RECOLECCIÓ D'ALIMENTS I ESCUTS.
    private void recolectar() {

        // System.out.println("dins recolectar ");
        if (!observar() && estat.recursosAgafats <= 5) {
            tencGana--;
            faigVolta = false;
            if (tencGana <= 9) {
                System.out.println("sonar:" + tencGana);
                if (tencGana == 0) {
                    tencGana = 45;
                }
                atura();
                gira(36);
                faigVolta = true;
            }
            //Hyper pq no troba res nomes un pic
            if (noVeigRes == 300 && estat.recursosAgafats >= 4) {
                noVeigRes = 0;
                if (estat.hiperespais > 0 && (estat.forces >= 11000 || estat.recursosAgafats < 4)) {
                    if (estat.forces > 6000 && !estat.hiperEspaiActiu) {
                        hyperespai();
                    }
                }
            }
        } else {
            tencGana = 45;
            noVeigRes = 0;
            faigVolta = false;
        }
    }

    private boolean observar() {
        //  System.out.println("Dins observar");
        posicioArray = -1;
        for (int i = 0; i < estat.numObjectes; i++) {
            if (esRecursPersonal(i)) {
                //  System.out.println("El recurs es positiu");
                if (menorDistancia(i, minim)) {
                    minim = estat.objectes[i].agafaDistancia();
                    posicioArray = i;
                }
            } else if (noEsRecursPersonal(i)) {
                //  System.out.println("El recurs es negatiu");
                if (repetirMirar2 == 0) {
                    repetirMirar2 = 2;
                    mira(estat.objectes[i]);
                    if (!estat.llançant) {
                        llança();
                    }
                    noVeigRes = 0;
                } else {
                    repetirMirar2--;
                }
            }
        }
        minim = Integer.MAX_VALUE;
        if (posicioArray != -1) {
            if (repetirMirar == 0) {
                repetirMirar = 2;
                //  System.out.println("MIRAM");
                mira(estat.objectes[posicioArray]);
                noVeigRes = 0;
            } else {
                //System.out.println("decrementam mirar");
                repetirMirar--;
            }
            return true;
        }
        // System.out.println("NO VEIG RES VAL: " + noVeigRes);
        noVeigRes++;
        return false;

    }

    private boolean esRecursPersonal(int i) {

        if ((estat.objectes[i].agafaSector() == 2
                || estat.objectes[i].agafaSector() == 3)) {
            if (estat.objectes[i].agafaTipus() == 100 + estat.id) {
                return true;
            } else if (estat.objectes[i].agafaTipus() == Estat.ESCUT) {
                return (estat.recursosAgafats >= 5);
            }
        }
        return false;
    }

    private boolean noEsRecursPersonal(int i) {
        return ((estat.objectes[i].agafaSector() == 2 || estat.objectes[i].agafaSector() == 3) && (estat.objectes[i].agafaTipus() >= 100)
                && (estat.objectes[i].agafaTipus() != (100 + estat.id)) && (estat.objectes[i].agafaDistancia() <= 350));
    }

    private boolean menorDistancia(int i, int minim) {
        return minim > estat.objectes[i].agafaDistancia();
    }

    private void disparEnemicDetectat() {

        if (estat.llançamentEnemicDetectat && estat.distanciaLlançamentEnemic <= 100) {
            if (estat.escuts > 0 && !estat.escutActivat) {
                activaEscut();
            } else if (estat.hiperespais > 0 && !estat.hiperEspaiActiu) {
                hyperespai();
            }
        }

    }

    /*
            if (estat.llançamentEnemicDetectat && estat.distanciaLlançamentEnemic < 100) {
            if (estat.hiperespais > 0 && estat.hiperespais >= estat.escuts && estat.forces >= 10000) {
                hyperespai();
                System.out.println("hyper perque condicons favorbales");
            } else if (estat.escuts > 0) {
                activaEscut();
                System.out.println("Escut perque hyper no conve");
            } else if (estat.hiperespais > 0) {
                //Cas hyper > estats i forces < 4500
                System.out.println("Hyper pq no donam per mes");
                hyperespai();
            }
        }
     */
}