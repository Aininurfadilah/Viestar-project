'use strict';

const firebase = require('../db');
const vocab = require('../models/vocab');
const firestore = firebase.firestore();


const addVocab = async (req, res, next) => {
    try {
        const data = req.body;
        await firestore.collection('vocab').doc().set(data);
        res.send('Record saved successfuly');
    } catch (error) {
        res.status(400).send(error.message);
    }
}

const getAllVocabs = async (req, res, next) => {
    try {
        const vocabs = await firestore.collection('vocab');
        const data = await vocabs.get();
        const vocabsArray = [];
        if(data.empty) {
            res.status(404).send('No record found');
        }else {
            data.forEach(doc => {
                const vocab = new vocab(
                    doc.idVideo,
                    doc.data().subtitle,
                    doc.data().outTranslate,
                    
                );
                vocabsArray.push(vocab);
            });
            res.send(vocabsArray);
        }
    } catch (error) {
        res.status(400).send(error.message);
    }
}

const getVocab = async (req, res, next) => {
    try {
        const id = req.params.id;
        const vocab = await firestore.collection('vocabs').doc(id);
        const data = await vocab.get();
        if(!data.exists) {
            res.status(404).send('vocab with the given ID not found');
        }else {
            res.send(data.data());
        }
    } catch (error) {
        res.status(400).send(error.message);
    }
}

const updateVocab = async (req, res, next) => {
    try {
        const id = req.params.id;
        const data = req.body;
        const vocab =  await firestore.collection('vocabs').doc(id);
        await vocab.update(data);
        res.send('vocab record updated successfuly');        
    } catch (error) {
        res.status(400).send(error.message);
    }
}

const deleteVocab = async (req, res, next) => {
    try {
        const id = req.params.id;
        await firestore.collection('vocabs').doc(id).delete();
        res.send('Record deleted successfuly');
    } catch (error) {
        res.status(400).send(error.message);
    }
}

module.exports = {
    addVocab,
    getAllVocabs,
    getVocab,
    updateVocab,
    deleteVocab
}