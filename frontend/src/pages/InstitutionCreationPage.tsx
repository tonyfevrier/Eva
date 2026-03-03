import React, { useState, type Dispatch, type SetStateAction } from "react";
import { Input } from "../components/Input"
import { Select } from "../components/Select"
import { Textarea } from "../components/Textarea"

export type InstitutionCreationData = {
    name: string,
    town: string,
    category: string,
    contactMail: string,
    studentsNumber: string,
    socialStatus: string,
    institutionSpecifities: string,
    studentsSpecificities: string,
    teachersSpecificities: string,
}

type PageData = {
    formData: InstitutionCreationData,
    setData: Dispatch<SetStateAction<InstitutionCreationData>>
}

export function InstitutionCreationPage({formData, setData}:PageData){

    const handleChange = (e:React.ChangeEvent<any>) => {
        const {name, value} = e.target;
        const updatedFormData = {...formData, [name]: value};
        setData(updatedFormData);
    }
     
    return <form>
                <Input title="Nom de l'établissement" name="name" type="text" value={formData.name} onChange={handleChange} required/>
                <Input title="Ville" name="town" type="text" value={formData.town} onChange={handleChange}/>
                <Input title="Mail de contact" name="contactMail" type="mail" value={formData.contactMail} onChange={handleChange} required/>
                <Select title="Type" name="category" value={formData.category} onChange={handleChange} required>
                    <option value="">Choisissez une des options suivantes</option>
                    <option value="Public">Public</option>
                    <option value="Privé">Privé</option>
                    <option value="Privé hors contrat">Privé hors contrat</option>
                    <option value="Autre">Autre</option>
                </Select>
                <Select title="Niveau socio-économique moyen des apprenants" name="socialStatus" value={formData.socialStatus} onChange={handleChange} required>
                    <option value="">Choisissez une des options suivantes</option>
                    <option value="Très faible">Très faible</option>
                    <option value="Faible">Faible</option>
                    <option value="Moyen">Moyen</option>
                    <option value="Elevé">Elevé</option>
                    <option value="Très élevé">Très élevé</option>
                </Select>
                <Input title="Nombre approximatif d'étudiants" type="text" name="studentsNumber" value={formData.studentsNumber} onChange={handleChange} required/>
                <Textarea title="Particularités de l'établissement" name="institutionSpecifities" value={formData.institutionSpecifities} onChange={handleChange}/>
                <Textarea title="Particularités des apprenants" name="studentsSpecificities" value={formData.studentsSpecificities} onChange={handleChange}/>
                <Textarea title="Particularités des enseignants" name="teachersSpecificities" value={formData.teachersSpecificities} onChange={handleChange}/>
            </form>      
}
 