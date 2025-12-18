type ModalProps = {
    title?: string,
    postTitle: string,
    postContent: string,
    onSave: () => void,
    onClose: () => void
}

export function Modal({title = "Edit post", postTitle, postContent, onSave, onClose}: ModalProps){
    return <div className="modal show" tabIndex={-1} style={{display: "block"}}>
                <div className="modal-dialog">
                    <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">{title}</h5>
                        <button className="btn btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={onClose}></button>
                    </div>
                    <div className="modal-body">
                        <input type="text" defaultValue={postTitle} className="form-control form-control-lg mb-3" />
                        <textarea defaultValue={postContent} className="form-control form-control-lg" rows={4}></textarea>
                    </div>
                    <div className="modal-footer">
                        <button onClick={onClose}>Annuler</button>
                        <button onClick={onSave}>Confirmer</button>
                    </div>
                    </div>
                </div>
            </div>
            
}

Modal.displayName = 'Modal';