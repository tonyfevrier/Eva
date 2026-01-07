import { Input } from "../components/Input";
import { Button } from "../components/Button";
import { useState } from "react";

type DescribeForm = {
    affiliation: string,
    street: string,
    postcode: string,
    town: string,
    phone: string,
    acceptMap: boolean,
    acceptContact: boolean
}

export function DescribePage(){
    /*
    Etats nécessaires : 
    texte des input textes
    Etat de valeur des deux checkbox

    */

    const initialformData = {affiliation: "", street: "",  postcode: "", town: "",
                             phone: "", acceptMap: false, acceptContact: false};
    const [formData, setFormData] = useState<DescribeForm>(initialformData);

    return <>
                <h1>Te décrire</h1>
                <p> Votre inscription a bien été réalisée. 
                    Il vous reste quelques informations de profils à compléter.
                </p>
                <form onSubmit={()=>{}}>
                    <Input title="Affiliation" name="affiliation" type="text" value={formData.affiliation} onChange={(e)=>{setFormData({...formData, affiliation: e.target.value})}}/>
                    <Input title="Etes-vous d'accord pour que votre localisation apparaisse sur une carte?" name="card-accept" type="checkbox" onChange={() => setFormData({...formData, acceptMap: !formData.acceptMap})}/>
                    <Input title="Rue" name="rue" type="text" value={formData.street} onChange={(e)=>{setFormData({...formData, street: e.target.value})}} disabled={!formData.acceptMap}/>
                    <Input title="Code postal" name="postcode" type="text" value={formData.postcode} onChange={(e)=>{setFormData({...formData, postcode: e.target.value})}} disabled={!formData.acceptMap}/>
                    <Input title="Ville" name="ville" type="text" value={formData.town} onChange={(e)=>{setFormData({...formData, town: e.target.value})}} disabled={!formData.acceptMap}/>
                    <Input title="Etes-vous d'accord pour que d'autres enseignants puissent vous contacter par email? Si oui vous pourrez rentrer votre numéro de téléphone" name="card-accept" type="checkbox" onChange={() => setFormData({...formData, acceptContact: !formData.acceptContact})}/>
                    <Input title="Téléphone (facultatif)" name="téléphone" type="tel" value={formData.phone} onChange={(e)=>{setFormData({...formData, phone: e.target.value})}} disabled={!formData.acceptContact}/>
                    <Button disabled={formData.affiliation === ""}>Sauvegarder les informations</Button>
                </form>
           </>
}