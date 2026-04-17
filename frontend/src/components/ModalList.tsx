import type { PropsWithChildren } from "react";

type ModalProps = {
    title?: string,
    onClose: () => void
}

export function ModalList({title = "Edit post", children, onClose}: PropsWithChildren<ModalProps>){
    return <div className="modal show" tabIndex={-1} style={{display: "block"}}>
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title">{title}</h5>
                            <button className="btn btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={onClose}></button>
                        </div>
                        <div>
                            {children}
                        </div>
                    </div>
                </div>
            </div>
            
}

ModalList.displayName = 'ModalList';